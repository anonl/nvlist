package nl.weeaboo.vn.desktop.debug;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

/**
 * Starts/stops listening for incoming debug adapter protocol (DAP) connections.
 */
public final class NvlistDebugLauncher implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistDebugLauncher.class);

    private final int listenPort;
    private final INvlistTaskRunner taskRunner;
    private final Thread acceptorThread;

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
        LOG.info("Debug mode requested, starting debug adapter server on port {}");

        NvlistDebugLauncher launcher = new NvlistDebugLauncher(listenPort, new NvlistTaskRunner(nvlistAccessExecutor));
        launcher.acceptorThread.start();
        return launcher;
    }

    @Override
    public void close() {
        acceptorThread.interrupt();
        try {
            acceptorThread.join();
        } catch (InterruptedException e) {
            LOG.warn("Wait for acceptor thread shutdown was interrupted", e);
        }
    }

    private void acceptorLoop() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    NvlistDebugServer.start(taskRunner, socket);
                } catch (IOException e) {
                    LOG.warn("[debug-server] I/O error while trying to accept a new connection", e);
                    Closeables.close(socket, true);
                }
            }
        } catch (IOException serverSocketException) {
            LOG.error("[debug-server] fatal I/O error", serverSocketException);
        } finally {
            LOG.info("[debug-server] terminated");
        }
    }

}
