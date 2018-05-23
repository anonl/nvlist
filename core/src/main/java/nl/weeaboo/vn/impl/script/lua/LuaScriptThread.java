package nl.weeaboo.vn.impl.script.lua;

import java.util.List;

import com.google.common.collect.ImmutableList;

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

public class LuaScriptThread implements IScriptThread {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    final Indirect<LuaThread> threadRef;

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
    public String toString() {
        LuaThread thread = threadRef.get();
        if (thread == null) {
            return "<no-thread-active>";
        }

        return String.valueOf(thread);
    }

    @Override
    public List<String> getStackTrace() {
        LuaThread thread = threadRef.get();
        if (thread == null) {
            return ImmutableList.of();
        }

        return LuaUtil.getLuaStack(thread);
    }

}
