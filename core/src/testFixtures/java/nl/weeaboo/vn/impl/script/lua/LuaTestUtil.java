package nl.weeaboo.vn.impl.script.lua;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.junit.Assert;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.luajava.CoerceLuaToJava;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.script.ThrowingScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;

/**
 * Various test test functions related to Lua.
 */
public final class LuaTestUtil {

    public static final FilePath SCRIPT_HELLOWORLD = FilePath.of("helloworld.lvn");
    public static final FilePath SCRIPT_SCRIPTLIB = FilePath.of("scriptlib.lvn");

    private LuaTestUtil() {
    }

    /** Creates a new run state and registers it on the current thread. */
    public static LuaRunState newRunState() {
        LuaRunState runState;
        try {
            runState = LuaRunState.create();
        } catch (LuaException e) {
            throw new AssertionError("Error instantiating Lua context", e);
        }
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

    /** Asserts that the Lua global with the given name has the specified value. */
    public static void assertGlobal(String name, Object val) {
        assertEquals(val, getGlobal(name));
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
    public static @Nullable <T> T getGlobal(String name, Class<T> type) {
        return getGlobal(name).touserdata(type);
    }

    /**
     * Sets a Lua global. Automatically converts the given Java value to an equivalent Lua value.
     *
     * @see #setGlobal(String, LuaValue)
     */
    public static void setGlobal(String name, Object javaObject) {
        setGlobal(name, CoerceJavaToLua.coerce(javaObject));
    }

    /**
     * Sets a Lua global.
     *
     * @see #setGlobal(String, Object)
     */
    public static void setGlobal(String name, LuaValue value) {
        LuaTable globals = LuaRunState.getCurrent().getGlobalEnvironment();
        globals.set(name, value);
    }

    /**
     * Asserts that a Lua object is equal to an expected Java object.
     */
    public static void assertEquals(Object expected, LuaValue luaValue) {
        if (expected == null) {
            Assert.assertTrue(luaValue.isnil());
        } else {
            Object javaValue = CoerceLuaToJava.coerceArg(luaValue, expected.getClass());
            Assert.assertEquals(expected, javaValue);
        }
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
     * @see #waitForAllThreads(IEnvironment)
     */
    public static void waitForAllThreads(INovel novel) {
        waitForAllThreads(() -> novel.getEnv());
    }

    /**
     * Runs all threads in all active contexts, until those contexts no longer contain any runnable threads.
     * @see #hasRunnableThreads(IScriptContext)
     */
    public static void waitForAllThreads(IEnvironment env) {
        waitForAllThreads(() -> env);
    }

    public static void waitForAllThreads(Supplier<IEnvironment> envSupplier) {
        int iteration = 0;
        while (iteration++ < 10_000) {
            IEnvironment env = envSupplier.get();
            boolean anyRunnableThreads = false;
            for (IContext context : env.getContextManager().getActiveContexts()) {
                if (hasRunnableThreads(context.getScriptContext())) {
                    anyRunnableThreads = true;
                    break;
                }
            }
            if (!anyRunnableThreads) {
                return;
            }

            env.update();
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
            scriptContext.updateThreads(context, ThrowingScriptExceptionHandler.INSTANCE);

            if (++iteration >= 10_000) {
                throw new AssertionError("One or more threads refuse to die");
            }
        }
    }

}
