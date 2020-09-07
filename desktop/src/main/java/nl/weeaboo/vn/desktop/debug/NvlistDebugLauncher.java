package nl.weeaboo.vn.desktop.debug;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.common.io.Closer;

/**
 * Starts/stops listening for incoming debug adapter protocol (DAP) connections.
 */
public final class NvlistDebugLauncher implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistDebugLauncher.class);

    private final int listenPort;
    private final INvlistTaskRunner taskRunner;
    private final Thread acceptorThread;

    private volatile ServerSocket serverSocket;

    NvlistDebugLauncher(int listenPort, INvlistTaskRunner taskRunner) {
        this.listenPort = listenPort;
        this.taskRunner = Objects.requireNonNull(taskRunner);

        acceptorThread = new Thread(this::acceptorLoop);
        acceptorThread.setName("debug-server-" + listenPort);
        acceptorThread.setDaemon(true);
    }

    /**
     * Starts listening for incoming debug adapter protocol (DAP) connections.
     *
     * @param listenPort Listen for incoming connections on this port.
     * @param nvlistAccessExecutor Access NVList internal structures only through tasks running on this executor.
     */
    public static NvlistDebugLauncher launch(int listenPort, Executor nvlistAccessExecutor) {
        LOG.info("Debug mode requested, starting debug adapter server on port {}", listenPort);

        NvlistDebugLauncher launcher = new NvlistDebugLauncher(listenPort, new NvlistTaskRunner(nvlistAccessExecutor));
        launcher.acceptorThread.start();
        return launcher;
    }

    @Override
    public void close() {
        try {
            Closeables.close(serverSocket, true);
            acceptorThread.interrupt();
            acceptorThread.join();
        } catch (IOException | InterruptedException e) {
            LOG.warn("Acceptor thread shutdown failed", e);
        }
    }

    private void acceptorLoop() {
        Closer closer = Closer.create();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            closer.register(serverSocket = new ServerSocket(listenPort));
            LOG.info("[debug-server] Listening for incoming connections on {}", listenPort);
            while (!serverSocket.isClosed()) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    LOG.info("[debug-server] Accepted incoming connection from {}", socket.getRemoteSocketAddress());
                    closer.register(NvlistDebugServer.start(taskRunner, socket, executorService));
                } catch (IOException e) {
                    LOG.warn("[debug-server] I/O error while trying to accept a new connection", e);
                    Closeables.close(socket, true);
                }
            }
        } catch (IOException serverSocketException) {
            LOG.error("[debug-server] fatal I/O error", serverSocketException);
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                LOG.warn("Unable to close debug server", e);
            }
            executorService.shutdown();
            LOG.info("[debug-server] terminated");
        }
    }

}
