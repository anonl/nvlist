package nl.weeaboo.vn.impl.script.lua;

import org.junit.Assert;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.impl.script.TestScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;

public final class LuaTestUtil {

    public static final FilePath SCRIPT_HELLOWORLD = FilePath.of("helloworld.lvn");
    public static final FilePath SCRIPT_YIELD = FilePath.of("yield.lvn");
    public static final FilePath SCRIPT_CREATECONTEXT = FilePath.of("createcontext.lvn");
    public static final FilePath SCRIPT_SCRIPTLIB = FilePath.of("scriptlib.lvn");
    public static final FilePath SCRIPT_SETMODE = FilePath.of("setmode.lvn");

    private LuaTestUtil() {
    }

    /** Creates a new run state and registers it on the current thread. */
    public static LuaRunState newRunState() {
        LuaRunState runState = new LuaRunState();
        runState.registerOnThread();
        return runState;
    }

    /** Creates a new script loader. */
    public static LuaScriptLoader newScriptLoader(IEnvironment env) {
        return LuaScriptLoader.newInstance(env);
    }

    /** Asserts that the value of the Lua global with the given name is equal to the given int value. */
    public static void assertGlobal(String name, int val) {
        Assert.assertEquals(val, getGlobal(name).optint(0));
    }

    /** Asserts that the value of the Lua global with the given name is equal to the given value. */
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

    /** Returns the Lua global with the given name, or {@code LuaNil#NIL} if not found. */
    public static LuaValue getGlobal(String name) {
        LuaTable globals = LuaRunState.getCurrent().getGlobalEnvironment();
        return globals.get(name);
    }

    /**
     * Returns the Lua userdata global with the given name, and extracts the Java object from it. If the Lua global
     * isn't userdata, returns {@code null}.
     */
    public static <T> T getGlobal(String name, Class<T> type) {
        return getGlobal(name).touserdata(type);
    }

    /**
     * @return {@code true} if the script context contains one or more runnable threads.
     * @see IScriptThread#isRunnable()
     */
    public static boolean hasRunnableThreads(IScriptContext context) {
        for (IScriptThread thread : context.getThreads()) {
            if (thread.isRunnable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs all threads in all active contexts, until those contexts no longer contain any runnable threads.
     * @see #hasRunnableThreads(IScriptContext)
     */
    public static void waitForAllThreads(IContextManager contextManager) {
        int iteration = 0;
        while (iteration < 10_000) {
            boolean anyRunnableThreads = false;

            for (IContext context : contextManager.getActiveContexts()) {
                IScriptContext scriptContext = context.getScriptContext();
                if (hasRunnableThreads(scriptContext)) {
                    anyRunnableThreads = true;
                    scriptContext.updateThreads(context, TestScriptExceptionHandler.INSTANCE);
                }
            }

            if (!anyRunnableThreads) {
                return;
            }
        }
        throw new AssertionError("One or more threads refuse to die");
    }

    /**
     * Runs all threads in the given context, until it no longer contains any runnable threads.
     * @see #hasRunnableThreads(IScriptContext)
     */
    public static void waitForAllThreads(IContext context) {
        int iteration = 0;
        IScriptContext scriptContext = context.getScriptContext();
        while (hasRunnableThreads(scriptContext)) {
            scriptContext.updateThreads(context, TestScriptExceptionHandler.INSTANCE);

            if (++iteration >= 10_000) {
                throw new AssertionError("One or more threads refuse to die");
            }
        }
    }

}
