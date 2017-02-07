package nl.weeaboo.vn.impl.script.lua;

import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptFunctionStub extends LuaScriptFunction {

    private static final long serialVersionUID = 1L;

    private int callCount;

    public LuaScriptFunctionStub() {
        this(LuaConstants.NONE);
    }

    public LuaScriptFunctionStub(Varargs args) {
        super(null, args);
    }

    @Override
    public void call() throws ScriptException {
        callCount++;
    }

    @Override
    public LuaScriptThread callInNewThread() throws ScriptException {
        return new ThreadStub(this);
    }

    /** Returns the number of times this script function was called */
    public int getCallCount() {
        return callCount;
    }

    private static class ThreadStub extends LuaScriptThread {

        private static final long serialVersionUID = 1L;

        private final LuaScriptFunctionStub function;
        private boolean destroyed;

        public ThreadStub(LuaScriptFunctionStub f) {
            super(null);

            function = f;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public void call(LuaClosure func) throws ScriptException {
            func.invoke();
        }

        @Override
        public void update() throws ScriptException {
            function.call();
        }

        @Override
        public boolean isRunnable() {
            return !destroyed && function.getCallCount() < 1;
        }

    }
}
