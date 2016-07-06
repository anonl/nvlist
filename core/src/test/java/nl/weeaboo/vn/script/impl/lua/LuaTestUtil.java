package nl.weeaboo.vn.script.impl.lua;

import org.junit.Assert;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;

public final class LuaTestUtil {

    public static final String SCRIPT_HELLOWORLD = "helloworld.lvn";
    public static final String SCRIPT_YIELD = "yield.lvn";
    public static final String SCRIPT_CREATECONTEXT = "createcontext.lvn";
    public static final String SCRIPT_SCRIPTLIB = "scriptlib.lvn";
    public static final String SCRIPT_SETMODE = "setmode.lvn";

    private LuaTestUtil() {
    }

    public static LuaRunState newRunState() {
        LuaRunState runState = new LuaRunState();
        runState.registerOnThread();
        return runState;
    }

    public static LuaScriptLoader newScriptLoader(IEnvironment env) {
        return LuaScriptLoader.newInstance(env);
    }

    public static void assertGlobal(String name, int val) {
        Assert.assertEquals(val, getGlobal(name).optint(0));
    }
    public static void assertGlobal(String name, Object val) {
        LuaValue global = getGlobal(name);

        if (val instanceof Boolean) {
            Assert.assertEquals(val, global.toboolean());
        } else if (val instanceof String) {
            Assert.assertEquals(val, global.tojstring());
        } else {
            Assert.assertEquals(val, global.optuserdata(null));
        }
    }

    public static LuaValue getGlobal(String name) {
        LuaTable globals = LuaRunState.getCurrent().getGlobalEnvironment();
        return globals.get(name);
    }

    public static <T> T getGlobal(String name, Class<T> type) {
        return getGlobal(name).optuserdata(type, null);
    }
    public static boolean hasRunnableThreads(IScriptContext context) {
        for (IScriptThread thread : context.getThreads()) {
            if (thread.isRunnable()) {
                return true;
            }
        }
        return false;
    }

    public static void waitForAllThreads(IContextManager contextManager) {
        int iteration = 0;
        while (iteration < 10000) {
            boolean anyRunnableThreads = false;

            for (IContext context : contextManager.getActiveContexts()) {
                IScriptContext scriptContext = context.getScriptContext();
                if (hasRunnableThreads(scriptContext)) {
                    anyRunnableThreads = true;
                    scriptContext.updateThreads(context);
                }
            }

            if (!anyRunnableThreads) {
                return;
            }
        }
        throw new AssertionError("One or more threads refuse to die");
    }
    public static void waitForAllThreads(IContext context) {
        int iteration = 0;
        IScriptContext scriptContext = context.getScriptContext();
        while (hasRunnableThreads(scriptContext)) {
            scriptContext.updateThreads(context);

            if (++iteration >= 10000) {
                throw new AssertionError("One or more threads refuse to die");
            }
        }
    }

}
