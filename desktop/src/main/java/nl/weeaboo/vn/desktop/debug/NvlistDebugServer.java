package nl.weeaboo.vn.desktop.debug;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.WillCloseWhenClosed;

import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ContinueArguments;
import org.eclipse.lsp4j.debug.ContinueResponse;
import org.eclipse.lsp4j.debug.DisconnectArguments;
import org.eclipse.lsp4j.debug.EvaluateArguments;
import org.eclipse.lsp4j.debug.EvaluateArgumentsContext;
import org.eclipse.lsp4j.debug.EvaluateResponse;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.NextArguments;
import org.eclipse.lsp4j.debug.PauseArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsResponse;
import org.eclipse.lsp4j.debug.StackTraceArguments;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.StepInArguments;
import org.eclipse.lsp4j.debug.StepOutArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArgumentsReason;
import org.eclipse.lsp4j.debug.ThreadsResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.debug.DebugLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Main debug adapter protocol implementation for NVList.
 */
final class NvlistDebugServer implements IDebugProtocolServer, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistDebugServer.class);
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("debug-server-scheduler").setDaemon(true).build());

    private final INvlistTaskRunner taskRunner;
    private final Breakpoints breakpoints = new Breakpoints();
    private final ActiveThreads activeThreads = new ActiveThreads(breakpoints);
    private Future<?> periodicUpdateTask = CompletableFuture.completedFuture(null);

    private IDebugProtocolClient peer;
    private Socket socket;
    private Future<Void> messageHandler;

    private NvlistDebugServer(INvlistTaskRunner taskRunner) {
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Override
    public CompletableFuture<Capabilities> initialize(InitializeRequestArguments args) {
        periodicUpdateTask.cancel(true);
        periodicUpdateTask = SCHEDULER.scheduleWithFixedDelay(() -> {
            taskRunner.runOnNvlistThread(this::update);
        }, 1, 1, TimeUnit.SECONDS);

        Capabilities caps = new Capabilities();
        caps.setSupportsEvaluateForHovers(true);
        return CompletableFuture.completedFuture(caps);
    }

    @Override
    public void close() {
        periodicUpdateTask.cancel(true);
        messageHandler.cancel(true);
        try {
            socket.close();
        } catch (IOException e) {
            LOG.warn("I/O exception trying to close debug server", e);
        }
    }

    private void update() {
        INovel novel = getNovel();
        if (novel != null) {
            activeThreads.update(novel, peer);
        }
    }

    @Override
    public CompletableFuture<Void> disconnect(DisconnectArguments args) {
        LOG.debug("[debug-server] Received disconnect request {}", args);

        close();
        System.exit(0);
        return CompletableFuture.completedFuture(null);
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
    public CompletableFuture<Void> next(NextArguments args) {
        return taskRunner.runOnNvlistThread(() -> {
            DebugThread thread = activeThreads.findById(args.getThreadId());
            LOG.debug("[debug-server] Received next request {}: {}", args.getThreadId(), thread);

            if (thread != null) {
                thread.step(EStepMode.NEXT);
            }
        });
    }

    @Override
    public CompletableFuture<Void> stepIn(StepInArguments args) {
        return taskRunner.runOnNvlistThread(() -> {
            DebugThread thread = activeThreads.findById(args.getThreadId());
            LOG.debug("[debug-server] Received step-in request {}: {}", args.getThreadId(), thread);

            if (thread != null) {
                thread.step(EStepMode.IN);
            }
        });
    }

    @Override
    public CompletableFuture<Void> stepOut(StepOutArguments args) {
        return taskRunner.runOnNvlistThread(() -> {
            DebugThread thread = activeThreads.findById(args.getThreadId());
            LOG.debug("[debug-server] Received step-out request {}: {}", args.getThreadId(), thread);

            if (thread != null) {
                thread.step(EStepMode.OUT);
            }
        });
    }

    @Override
    public CompletableFuture<ThreadsResponse> threads() {
        return taskRunner.supplyOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received threads request");

            update();

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
    public CompletableFuture<SetBreakpointsResponse> setBreakpoints(SetBreakpointsArguments args) {
        return taskRunner.supplyOnNvlistThread(() -> {
            LOG.debug("[debug-server] Received setBreakpoints request for {}", args.getSource());

            Breakpoint[] resultBreakpoints = breakpoints.setBreakpoints(args.getSource(), args.getBreakpoints());

            SetBreakpointsResponse response = new SetBreakpointsResponse();
            response.setBreakpoints(resultBreakpoints);
            return response;
        });
    }

    @Override
    public CompletableFuture<EvaluateResponse> evaluate(EvaluateArguments args) {
        return taskRunner.supplyOnNvlistThread(() -> {
            String context = args.getContext();
            String expr = args.getExpression();
            LOG.debug("[debug-server] Received evaluate request: context={}, expr={}", context, expr);

            INovel novel = getNovel();
            Preconditions.checkNotNull(novel, "NVList isn't active");

            IContextManager contextManager = novel.getEnv().getContextManager();
            DebugThread debugThread = activeThreads.findByFrameId(args.getFrameId());
            Preconditions.checkNotNull(novel, "NVList isn't active");

            EvaluateResponse response = new EvaluateResponse();
            try {
                LOG.debug("Evaluating in {} ({}): {}", debugThread, context, expr);
                switch (context) {
                case EvaluateArgumentsContext.REPL:
                    response.setResult(LuaScriptUtil.eval(contextManager, debugThread.getThread(), expr));
                    break;
                case EvaluateArgumentsContext.HOVER:
                    // Only run expressions without side-effects
                    // This regex-based implementation is rather crude and should be improved
                    if (expr.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                        response.setResult(LuaScriptUtil.eval(contextManager, debugThread.getThread(), expr));
                    }
                    break;
                }
            } catch (ScriptException e) {
                LOG.trace("Error evaluating expression: " + expr, e);
                LOG.warn("Error evaluating expression: " + expr + " :: " + e);
            }
            return response;
        });
    }

    @CheckForNull
    private INovel getNovel() {
        return StaticEnvironment.NOVEL.getIfPresent();
    }

    public static NvlistDebugServer start(INvlistTaskRunner taskRunner, @WillCloseWhenClosed Socket socket,
            ExecutorService executorService) throws IOException {

        NvlistDebugServer debugServer = new NvlistDebugServer(taskRunner);
        Launcher<IDebugProtocolClient> launcher = new DebugLauncher.Builder<IDebugProtocolClient>()
                .setLocalService(debugServer)
                .setRemoteInterface(IDebugProtocolClient.class)
                .setInput(socket.getInputStream())
                .setOutput(socket.getOutputStream())
                .setExecutorService(executorService)
                .create();
        debugServer.peer = launcher.getRemoteProxy();
        debugServer.socket = socket;
        debugServer.messageHandler = launcher.startListening();
        return debugServer;
    }

}
