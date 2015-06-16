package nl.weeaboo.vn.script.lua;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.LuaTable;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.TestContextBuilder;
import nl.weeaboo.vn.TestFileSystem;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.impl.Context;
import nl.weeaboo.vn.core.impl.ContextManager;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.script.lvn.LvnParseException;

public class BaseScriptTest {

    private LuaScriptLoader scriptLoader;
    private LuaScriptEnv scriptEnv;

    @Before
    public void init() throws LuaException {
        LuaRunState runState = LuaTestUtil.newRunState();
        scriptLoader = LuaTestUtil.newScriptLoader(TestFileSystem.newInstance());

        scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        scriptEnv.initEnv();
    }

    @After
    public void deinit() {
        scriptEnv.getRunState().destroy();
    }

    /** Create a dummy thread and run it for one frame */
    @Test
    public void threads() throws ScriptException {
        LuaScriptContext scriptContext = new LuaScriptContext(scriptEnv);

        LuaScriptFunctionStub function = new LuaScriptFunctionStub();
        IScriptThread thread = scriptContext.newThread(function);
        thread.update();

        Assert.assertEquals(1, function.getCallCount());
    }

    /** Make sure .lvn loading isn't completely broken. */
    @Test
    public void loadScript() throws LvnParseException, IOException {
        String normalized = scriptLoader.findScriptFile(LuaTestUtil.SCRIPT_HELLOWORLD);

        ICompiledLvnFile compiled;
        InputStream in = scriptLoader.openScript(normalized);
        try {
            compiled = scriptLoader.compileScript(normalized, in);
        } finally {
            in.close();
        }

        Assert.assertTrue(compiled.countTextLines(true) > 0);
    }

    /** Simple hello world script */
    @Test
    public void helloWorld() throws IOException, ScriptException {
        LuaScriptContext context = new LuaScriptContext(scriptEnv);
        IScriptThread mainThread = context.getMainThread();

        scriptLoader.loadScript(mainThread, LuaTestUtil.SCRIPT_HELLOWORLD);
    }

    /** Test behavior of yield function */
    @Test
    public void yield() throws IOException, ScriptException {
        LuaScriptContext context = new LuaScriptContext(scriptEnv);
        IScriptThread mainThread = context.getMainThread();

        scriptLoader.loadScript(mainThread, LuaTestUtil.SCRIPT_YIELD);
        LuaTestUtil.assertGlobal("count", 0); // Stop at yield(1)
        mainThread.update();
        LuaTestUtil.assertGlobal("count", 1); // Stop at yield(2)
        mainThread.update();
        LuaTestUtil.assertGlobal("count", 1);
        mainThread.update();
        for (int n = 0; n < 5; n++) {
            LuaTestUtil.assertGlobal("count", 3); // Stop at yield(5)
            mainThread.update();
        }
        LuaTestUtil.assertGlobal("count", 8); // Finished
    }

    @Test
    public void createContext() throws IOException, ScriptException {
        TestContextBuilder contextBuilder = new TestContextBuilder(scriptEnv);
        final ContextManager contextManager = new ContextManager(contextBuilder);

        // Make context manager available to the script environment
        LuaTable globals = contextBuilder.scriptEnv.getGlobals();
        globals.rawset("contextManager", LuajavaLib.toUserdata(contextManager, IContextManager.class));

        // Create an initial context and activate it
        Context mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);

        IScriptContext mainScriptContext = mainContext.getScriptContext();
        IScriptThread mainThread = mainScriptContext.getMainThread();

        Assert.assertEquals(1, contextManager.getActiveContexts().size());

        // Run a script that creates and activates a context
        scriptLoader.loadScript(mainThread, LuaTestUtil.SCRIPT_CREATECONTEXT);
        Assert.assertEquals(2, contextManager.getActiveContexts().size());

        LuaTestUtil.waitForAllThreads(contextManager);

        LuaTestUtil.assertGlobal("fooCalled", 1); // Spawned thread was executed
        LuaTestUtil.assertGlobal("waitFrames", 6); // It took 5+1 frames for spawned thread to die
    }

}
