package nl.weeaboo.vn.script.impl.lua;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.impl.TestEnvironment;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptUtilTest {

    private TestEnvironment env;
    private IContext mainContext;

    @Before
    public void init() throws ScriptException {
        env = TestEnvironment.newInstance();
        env.getScriptEnv().initEnv();
        mainContext = env.getContextManager().createContext();
    }

    @After
    public void deinit() {
        env.destroy();
    }

    @Test
    public void isLvnFile() {
        Assert.assertFalse(LuaScriptUtil.isLvnFile("test.lua"));
        Assert.assertTrue(LuaScriptUtil.isLvnFile("test.lvn"));
        Assert.assertFalse(LuaScriptUtil.isLvnFile("test.lvn.txt"));
    }

    @Test
    public void toScriptException() {
        LuaException original = LuaException.wrap("message", new RuntimeException("cause"));

        ScriptException converted = LuaScriptUtil.toScriptException("newMessage", original);

        // The conversion doesn't wrap the entire exception in another layer!
        Assert.assertEquals("newMessage: message: cause", converted.getMessage());
        Assert.assertSame(original.getCause(), converted.getCause());
    }

    /** Test for loadScript/callFunction/eval functions */
    @Test
    public void loadCallEval() throws IOException, ScriptException {
        IScriptThread mainThread = mainContext.getScriptContext().getMainThread();

        // Load script with some test functions
        LuaScriptUtil.loadScript(mainContext, env.getScriptLoader(), "script-util-test");

        // Call test function
        LuaScriptUtil.callFunction(mainContext, "x", 1, 2, 3);

        // Check that parameters were passed and the function was executed
        LuaTestUtil.assertGlobal("test", 1 + 2 + 3);

        // Main thread is paused in the yield call on line 15
        Assert.assertEquals(Arrays.asList("script-util-test.lvn:15"), mainThread.getStackTrace());

        // Run some arbitrary code
        LuaScriptUtil.eval(mainContext, "test = 42");
        LuaTestUtil.assertGlobal("test", 42);
        // After eval completes, the thread is still stuck in the same position as before
        Assert.assertEquals(Arrays.asList("script-util-test.lvn:15"), mainThread.getStackTrace());
    }

}
