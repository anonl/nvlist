package nl.weeaboo.vn.desktop.debug;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.annotation.WillCloseWhenClosed;

import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ContinueArguments;
import org.eclipse.lsp4j.debug.ContinueResponse;
import org.eclipse.lsp4j.debug.DisconnectArguments;
import org.eclipse.lsp4j.debug.EvaluateArguments;
import org.eclipse.lsp4j.debug.EvaluateResponse;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.PauseArguments;
import org.eclipse.lsp4j.debug.StackTraceArguments;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArgumentsReason;
import org.eclipse.lsp4j.debug.ThreadsResponse;
import org.eclipse.lsp4j.debug.launch.DSPLauncher;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.core.StaticEnvironment;

/**
 * Main debug adapter protocol implementation for NVList.
 */
final class NvlistDebugServer implements IDebugProtocolServer, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistDebugServer.class);

    private final ActiveThreads activeThreads = new ActiveThreads();

    private final INvlistTaskRunner taskRunner;

    private IDebugProtocolClient peer;
    private Socket socket;
    private Future<Void> messageHandler;

    public NvlistDebugServer(INvlistTaskRunner taskRunner) {
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    @Override
    public CompletableFuture<Capabilities> initialize(InitializeRequestArguments args) {
        Capabilities caps = new Capabilities();
        caps.setSupportsEvaluateForHovers(true);
        return CompletableFuture.completedFuture(caps);
    }

    @Override
    public void close() {
        messageHandler.cancel(true);
        try {
            socket.close();
        } catch (IOException e) {
            LOG.warn("I/O exception trying to close debug server", e);
        }
    }

    @Override
    public CompletableFuture<Void> disconnect(DisconnectArguments args) {
        close();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> launch(Map<String, Object> args) {
        return taskRunner.runOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received launch request");
        });
    }

    @Override
    public CompletableFuture<Void> pause(PauseArguments args) {
        return taskRunner.runOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received pause request {}", args.getThreadId());

            DebugThread thread = activeThreads.findById(args.getThreadId());
            if (thread != null) {
                thread.pause();

                StoppedEventArguments stopEvent = new StoppedEventArguments();
                stopEvent.setReason(StoppedEventArgumentsReason.PAUSE);
                stopEvent.setThreadId(args.getThreadId());
                peer.stopped(stopEvent);
            }
        });
    }

    @Override
    public CompletableFuture<ContinueResponse> continue_(ContinueArguments args) {
        return taskRunner.supplyOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received continue request {}", args.getThreadId());

            DebugThread thread = activeThreads.findById(args.getThreadId());
            if (thread != null) {
                thread.unpause();
            }

            ContinueResponse response = new ContinueResponse();
            return response;
        });
    }

    @Override
    public CompletableFuture<ThreadsResponse> threads() {
        return taskRunner.supplyOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received threads request");

            activeThreads.updateFrom(getNovel());
            List<org.eclipse.lsp4j.debug.Thread> threads = new ArrayList<>();
            for (DebugThread thread : activeThreads) {
                threads.add(thread.toDapThread());
            }

            ThreadsResponse response = new ThreadsResponse();
            response.setThreads(threads.toArray(new org.eclipse.lsp4j.debug.Thread[0]));
            return response;
        });
    }

    @Override
    public CompletableFuture<StackTraceResponse> stackTrace(StackTraceArguments args) {
        return taskRunner.supplyOnNvlistThread(() -> {
            int threadId = args.getThreadId();
            LOG.debug("[debug-server] Received stackTrace request for thread #{}", threadId);

            StackTraceResponse response = new StackTraceResponse();
            DebugThread thread = activeThreads.findById(threadId);
            if (thread != null) {
                response.setStackFrames(thread.getStackTrace());
            }
            return response;
        });
    }

    @Override
    public CompletableFuture<EvaluateResponse> evaluate(EvaluateArguments args) {
        return taskRunner.supplyOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received evaluate request {}", args.getExpression());

            EvaluateResponse response = new EvaluateResponse();
            response.setResult("???");
            return response;
        });
    }

    private INovel getNovel() {
        return StaticEnvironment.NOVEL.get();
    }

    public static NvlistDebugServer start(INvlistTaskRunner taskRunner, @WillCloseWhenClosed Socket socket)
            throws IOException {

        NvlistDebugServer debugServer = new NvlistDebugServer(taskRunner);
        Launcher<IDebugProtocolClient> launcher = DSPLauncher.createServerLauncher(
                debugServer, socket.getInputStream(), socket.getOutputStream());
        debugServer.peer = launcher.getRemoteProxy();
        debugServer.socket = socket;
        debugServer.messageHandler = launcher.startListening();
        return debugServer;
    }

}
