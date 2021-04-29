package nl.weeaboo.vn.impl.script.lua;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.ScriptException;

class LuaScriptFunction implements IScriptFunction {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    private final LuaClosure func;
    private final Varargs args;

    public LuaScriptFunction(LuaClosure func, Varargs args) {
        this.func = func;
        this.args = args;
    }

    @Override
    public void call() throws ScriptException {
        call(LuaThread.getRunning());
    }

    void call(LuaThread thread) throws ScriptException {
        LuaRunState runState = LuaImpl.getRunState();
        if (runState == null || thread == null) {
            throw new ScriptException("Unable to call Lua function -- no thread is running");
        }

        try {
            thread.callFunctionInThread(func, args);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error calling function: " + this, e);
        }
    }

    ILuaScriptThread callInNewThread() throws ScriptException {
        LuaRunState runState = LuaImpl.getRunState();
        LuaThread thread = runState.newThread(func, args);
        return new LuaScriptThread(thread);
    }

    protected Varargs getArgs() {
        return args;
    }

}
