package nl.weeaboo.vn.desktop.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ContinueArguments;
import org.eclipse.lsp4j.debug.DisconnectArguments;
import org.eclipse.lsp4j.debug.EvaluateArguments;
import org.eclipse.lsp4j.debug.EvaluateArgumentsContext;
import org.eclipse.lsp4j.debug.EvaluateResponse;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.NextArguments;
import org.eclipse.lsp4j.debug.PauseArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsResponse;
import org.eclipse.lsp4j.debug.SourceArguments;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.SourceResponse;
import org.eclipse.lsp4j.debug.StackTraceArguments;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.StepInArguments;
import org.eclipse.lsp4j.debug.StepOutArguments;
import org.eclipse.lsp4j.debug.Thread;
import org.eclipse.lsp4j.debug.ThreadsResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.debug.DebugLauncher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.MoreExecutors;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.desktop.debug.NvlistDebugServer.IConnection;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.NovelMock;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptThread;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.NoExitSecurityManager;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class NvlistDebugServerTest {

    @Rule
    public final Timeout timeout = new Timeout(30, TimeUnit.SECONDS);

    private final DapClient dapClient = new DapClient();
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private SecurityManager oldSecurityManager;
    private TestEnvironment env;
    private IContext context;
    private ILuaScriptThread mainThread;

    private Future<Void> clientFuture;
    private NvlistDebugServer debugServer;
    private IDebugProtocolServer remoteProxy;

    @Before
    public void before() throws IOException, ScriptException {
        oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());

        HeadlessGdx.init();
        env = TestEnvironment.newInstance();
        context = env.createActiveContext();
        StaticEnvironment.NOVEL.set(new NovelMock(env));

        LuaScriptEnv scriptEnv = env.getScriptEnv();
        scriptEnv.initEnv();
        LuaScriptUtil.loadScript(context, FilePath.of("main"));

        mainThread = (ILuaScriptThread)context.getScriptContext().getMainThread();

        PipedInputStream clientIn = new PipedInputStream();
        PipedOutputStream clientOut = new PipedOutputStream();
        PipedInputStream serverIn = new PipedInputStream(clientOut);
        PipedOutputStream serverOut = new PipedOutputStream(clientIn);

        Launcher<IDebugProtocolServer> clientLauncher = DebugLauncher.createLauncher(dapClient,
                IDebugProtocolServer.class, clientIn, clientOut);
        clientFuture = clientLauncher.startListening();

        debugServer = NvlistDebugServer.start(new NvlistTaskRunnerMock(scriptEnv), new IConnection() {
            @Override
            public void close() throws IOException {
                serverIn.close();
                serverOut.close();
            }

            @Override
            public OutputStream getOutputStream() {
                return serverOut;
            }

            @Override
            public InputStream getInputStream() {
                return serverIn;
            }
        }, executor);

        remoteProxy = clientLauncher.getRemoteProxy();
    }

    @After
    public void after() {
        try {
            clientFuture.cancel(true);
            debugServer.close();
            MoreExecutors.shutdownAndAwaitTermination(executor, 5, TimeUnit.SECONDS);
        } finally {
            System.setSecurityManager(oldSecurityManager);
        }

        StaticEnvironment.getInstance().clear();
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        Capabilities caps = remoteProxy.initialize(new InitializeRequestArguments()).get();
        Assert.assertEquals(true, caps.getSupportsEvaluateForHovers());

        debugServer.update();
        remoteProxy.launch(ImmutableMap.of("program", "a.lvn")).get();
        env.update(); // Allow a.lvn to run for one frame

        assertSourceRequest();
        assertThreadsRequest();
        assertStackTraceRequest();
        assertEvaluate();
        assertStepping();
        assertPauseContinue();

        // Disconnect request causes a disconnect, so there's no guarantee that a reply will be received
        @SuppressWarnings("unused")
        CompletableFuture<Void> result = remoteProxy.disconnect(new DisconnectArguments());
    }

    /** Requests the source code of a script file */
    private void assertSourceRequest() throws InterruptedException, ExecutionException {
        SourceArguments reqArgs = new SourceArguments();
        reqArgs.setSource(DapTestHelper.source("a.lvn"));
        SourceResponse sourceResponse = remoteProxy.source(reqArgs).get();
        Assert.assertEquals("#a.lvn\n@local x = 1\n@yield()", sourceResponse.getContent());

        // Requests for files that don't exist throw an exception
        reqArgs.setSource(DapTestHelper.source("doesntexist.lvn"));
        Assert.assertThrows(ExecutionException.class, () -> remoteProxy.source(reqArgs).get());
    }

    /** Requests the current threads */
    private void assertThreadsRequest() throws InterruptedException, ExecutionException {
        ThreadsResponse threadsResponse = remoteProxy.threads().get();
        Map<Integer, Thread> actualThreadsById = Stream.of(threadsResponse.getThreads())
                .collect(Collectors.toMap(t -> t.getId(), t -> t));

        Collection<? extends IScriptThread> expectedThreads = context.getScriptContext().getThreads();
        for (ILuaScriptThread expected : Iterables.filter(expectedThreads, ILuaScriptThread.class)) {
            Thread actual = actualThreadsById.get(expected.getThreadId());

            Assert.assertEquals(expected.getThreadId(), actual.getId());
            Assert.assertEquals(expected.getName(), actual.getName());
        }
    }

    /** Requests the current stack trace of a thread */
    private void assertStackTraceRequest() throws InterruptedException, ExecutionException {
        DebugThread debugThread = new DebugThread(mainThread, dapClient);
        StackTraceArguments reqArgs = new StackTraceArguments();
        reqArgs.setThreadId(debugThread.getThreadId());
        StackTraceResponse stackTraceResponse = remoteProxy.stackTrace(reqArgs).get();

        Assert.assertArrayEquals(debugThread.getStackTrace(), stackTraceResponse.getStackFrames());
    }

    /** Evaluates some code in the current context */
    private void assertEvaluate() throws InterruptedException, ExecutionException {
        EvaluateArguments reqArgs = new EvaluateArguments();
        reqArgs.setContext(EvaluateArgumentsContext.REPL);
        reqArgs.setExpression("x + 1");
        EvaluateResponse evalResponse = remoteProxy.evaluate(reqArgs).get();

        // The local variable x is set to 1 in a.lvn
        Assert.assertEquals("2", evalResponse.getResult());

        // Hover is also supported, but can only be used with simple expressions (mainly single variables)
        reqArgs.setContext(EvaluateArgumentsContext.HOVER);
        reqArgs.setExpression("x");
        evalResponse = remoteProxy.evaluate(reqArgs).get();
        Assert.assertEquals("1", evalResponse.getResult());
    }

    /**
     * Manually (un)pause a specific thread.
     */
    private void assertPauseContinue() throws InterruptedException, ExecutionException {
        ILuaScriptThread thread;
        try {
            thread = (ILuaScriptThread)context.getScriptContext()
                    .loadScriptInNewThread(FilePath.of("thread-pause.lvn"));
        } catch (ScriptException e) {
            throw new AssertionError(e);
        }
        debugServer.update(); // Allow the debug server to detect the new thread

        env.update();
        LuaTestUtil.assertGlobal("pos", 1);

        PauseArguments pauseArgs = new PauseArguments();
        pauseArgs.setThreadId(thread.getThreadId());
        remoteProxy.pause(pauseArgs).get();

        // The thread is paused and therefore doesn't run
        env.update();
        LuaTestUtil.assertGlobal("pos", 1);

        ContinueArguments continueArgs = new ContinueArguments();
        continueArgs.setThreadId(thread.getThreadId());
        remoteProxy.continue_(continueArgs).get();

        env.update();
        LuaTestUtil.assertGlobal("pos", 2);
    }

    /** Stepping after hitting a breakpoint */
    private void assertStepping() throws InterruptedException, ExecutionException {
        setBreakpoints();

        try {
            mainThread.eval("jump(\"breakpoints.lvn\")");
        } catch (ScriptException e) {
            throw new AssertionError(e);
        }

        debugServer.update();
        env.update();

        LuaTestUtil.assertGlobal("pos", 1);
        stepIn();
        LuaTestUtil.assertGlobal("pos", 2);

        stepIn(); // Step into sub()
        LuaTestUtil.assertGlobal("pos", 2);
        stepIn(); // Stop at first instruction in sub()
        LuaTestUtil.assertGlobal("pos", 2);
        stepIn(); // Stop at second instruction in sub
        LuaTestUtil.assertGlobal("pos", 3);

        stepNext(); // Skip over subsub()
        LuaTestUtil.assertGlobal("pos", 4);

        stepOut(); // Exit sub()
        LuaTestUtil.assertGlobal("pos", 5);
    }

    /** Checks breakpoint support */
    private void setBreakpoints() throws InterruptedException, ExecutionException {
        SetBreakpointsArguments reqArgs = new SetBreakpointsArguments();
        reqArgs.setSource(DapTestHelper.source("breakpoints.lvn"));
        reqArgs.setBreakpoints(new SourceBreakpoint[] { DapTestHelper.sourceBreakpoint(15) });
        SetBreakpointsResponse response = remoteProxy.setBreakpoints(reqArgs).get();

        Breakpoint bp = Iterables.getOnlyElement(Arrays.asList(response.getBreakpoints()));
        Assert.assertEquals(NameMapping.toAbsoluteScriptPath("breakpoints.lvn"), bp.getSource().getPath());
        Assert.assertEquals(Integer.valueOf(15), bp.getLine());
        Assert.assertEquals(true, bp.isVerified());
    }

    private void stepIn() throws InterruptedException, ExecutionException {
        StepInArguments stepInArgs = new StepInArguments();
        stepInArgs.setThreadId(mainThread.getThreadId());
        remoteProxy.stepIn(stepInArgs).get();
        env.update();
    }

    private void stepNext() throws InterruptedException, ExecutionException {
        NextArguments nextArgs = new NextArguments();
        nextArgs.setThreadId(mainThread.getThreadId());
        remoteProxy.next(nextArgs).get();
        env.update();
    }

    private void stepOut() throws InterruptedException, ExecutionException {
        StepOutArguments stepOutArgs = new StepOutArguments();
        stepOutArgs.setThreadId(mainThread.getThreadId());
        remoteProxy.stepOut(stepOutArgs).get();
        env.update();
    }

}
