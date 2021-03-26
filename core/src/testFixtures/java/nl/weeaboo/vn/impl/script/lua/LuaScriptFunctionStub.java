package nl.weeaboo.vn.impl.script.lua;

import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.Varargs;

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
    public void call() {
        callCount++;
    }

    @Override
    final void call(LuaThread thread) {
        call();
    }

    @Override
    public LuaScriptThread callInNewThread() {
        return new ThreadStub(this);
    }

    /** Returns the number of times this script function was called */
    public int getCallCount() {
        return callCount;
    }

    private static class ThreadStub extends LuaScriptThread {

        private static final long serialVersionUID = 1L;

        private final LuaScriptFunctionStub function;
        private boolean finished;
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
        public void call(LuaClosure func) {
            func.invoke();
        }

        @Override
        public void update() {
            if (finished) {
                return;
            }

            function.call();
            finished = true;
        }

        @Override
        public boolean isRunnable() {
            return !destroyed && !finished;
        }

    }
}
