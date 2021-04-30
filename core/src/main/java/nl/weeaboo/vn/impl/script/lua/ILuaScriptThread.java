package nl.weeaboo.vn.impl.script.lua;

import java.util.List;

import javax.annotation.CheckForNull;

import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaStackTraceElement;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public interface ILuaScriptThread extends IScriptThread {

    int getThreadId();

    /**
     * Returns the stack trace element at the given offset.
     * @param offset Skip the deepest {@code offset} levels of the call stack.
     */
    @CheckForNull
    LuaStackTraceElement stackTraceElem(int offset);

    /**
     * Generates a stack trace for this thread.
     */
    List<LuaStackTraceElement> stackTrace();

    /**
     * Runs Lua code on this thread.
     * @throws ScriptException If the Lua code can't be parsed, or throws an exception.
     */
    Varargs eval(String code) throws ScriptException;

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the function doesn't exist, or the Lua code throws an exception.
     */
    void call(String funcName, Object... args) throws ScriptException;

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the Lua function throws an exception.
     */
    void call(LuaScriptFunction func) throws ScriptException;

    /**
     * Calls a Lua function on this thread.
     * @throws ScriptException If the Lua function throws an exception.
     */
    void call(LuaClosure func) throws ScriptException;

    /**
     * Installs a debug event handler on this thread.
     */
    void installHook(ILuaDebugHook debugHook);

}
