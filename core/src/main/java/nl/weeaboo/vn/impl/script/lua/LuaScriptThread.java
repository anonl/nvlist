package nl.weeaboo.vn.impl.script.lua;

import java.util.List;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.core.Indirect;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Default implementation of {@link IScriptThread}.
 */
public class LuaScriptThread implements IScriptThread {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    final Indirect<LuaThread> threadRef;

    private transient boolean paused;

    LuaScriptThread(LuaThread thread) {
        this.threadRef = Indirect.of(thread);
    }

    @Override
    public void destroy() {
        threadRef.get().destroy();
    }

    @Override
    public boolean isDestroyed() {
        return threadRef.get().isDead();
    }

    /**
     * Runs Lua code on this thread.
     * @throws ScriptException If the Lua code can't be parsed, or throws an exception.
     */
    public Varargs eval(String code) throws ScriptException {
        LuaThread thread = threadRef.get();

        try {
            LuaClosure func = LuaUtil.compileForEval(code, thread.getfenv());
            return thread.callFunctionInThread(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the function doesn't exist, or the Lua code throws an exception.
     */
    public void call(String funcName, Object... args) throws ScriptException {
        LuaThread thread = threadRef.get();

        try {
            LuaClosure function = LuaUtil.getEntryForPath(thread, funcName).checkclosure();
            Varargs luaArgs = CoerceJavaToLua.coerceArgs(args);
            thread.callFunctionInThread(function, luaArgs);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the Lua function throws an exception.
     */
    public void call(LuaScriptFunction func) throws ScriptException {
        LuaThread thread = threadRef.get();

        func.call(thread);
    }

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the Lua function throws an exception.
     */
    public void call(LuaClosure func) throws ScriptException {
        LuaThread thread = threadRef.get();

        try {
            thread.callFunctionInThread(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    @Override
    public void update() throws ScriptException {
        if (paused) {
            return;
        }

        LuaThread thread = threadRef.get();

        if (!thread.isDead()) {
            try {
                thread.resume(LuaConstants.NONE);
            } catch (LuaException e) {
                throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
            }
        }
    }

    @Override
    public boolean isRunnable() {
        LuaThread thread = threadRef.get();

        return thread.isRunnable();
    }

    @Override
    public String getName() {
        return String.valueOf(threadRef.get());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public List<String> getStackTrace() {
        return LuaUtil.getLuaStack(threadRef.get());
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

}
