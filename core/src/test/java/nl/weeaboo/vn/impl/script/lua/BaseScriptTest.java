package nl.weeaboo.vn.impl.script.lua;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.ContextManager;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.script.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.impl.script.lvn.LvnParseException;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class BaseScriptTest {

    private TestEnvironment env;
    private LuaScriptLoader scriptLoader;
    private LuaScriptEnv scriptEnv;
    private IContext context;

    @Before
    public void init() throws ScriptException {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();
        scriptLoader = (LuaScriptLoader)env.getScriptEnv().getScriptLoader();
        scriptEnv = env.getScriptEnv();
        scriptEnv.initEnv();

        context = env.createActiveContext();
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
        IScriptThread thread = scriptContext.createThread(function);
        thread.update();

        Assert.assertEquals(1, function.getCallCount());
    }

    /** Make sure .lvn loading isn't completely broken. */
    @Test
    public void loadScript() throws LvnParseException, IOException {
        FilePath filename = LuaTestUtil.SCRIPT_HELLOWORLD;

        ICompiledLvnFile compiled;
        InputStream in = scriptLoader.openScript(filename);
        try {
            ResourceId resourceId = scriptLoader.resolveResource(filename);
            compiled = scriptLoader.compileScript(resourceId, in);
        } finally {
            in.close();
        }

        Assert.assertTrue(compiled.countTextLines(true) > 0);
    }

    /** Simple hello world script */
    @Test
    public void helloWorld() throws ScriptException, IOException {
        LuaScriptUtil.loadScript(context, LuaTestUtil.SCRIPT_HELLOWORLD);
    }

    /** Test behavior of yield function */
    @Test
    public void yield() throws ScriptException, IOException {
        IScriptThread mainThread = context.getScriptContext().getMainThread();

        LuaScriptUtil.loadScript(context, FilePath.of("yield.lvn"));
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
    public void createContext() throws ScriptException, IOException {
        ContextManager contextManager = env.getContextManager();

        // Make context manager available to the script environment
        LuaTable globals = scriptEnv.getGlobals();
        globals.rawset("contextManager", LuajavaLib.toUserdata(contextManager, ContextManager.class));

        Assert.assertEquals(1, contextManager.getActiveContexts().size());

        // Run a script that creates and activates a context
        LuaScriptUtil.loadScript(context, FilePath.of("createcontext.lvn"));
        Assert.assertEquals(2, contextManager.getActiveContexts().size());

        LuaTestUtil.waitForAllThreads(env);

        LuaTestUtil.assertGlobal("fooCalled", 1); // Spawned thread was executed
        LuaTestUtil.assertGlobal("waitFrames", 6); // It took 5+1 frames for spawned thread to die
    }

}
