package nl.weeaboo.vn.impl.script;

import java.util.concurrent.atomic.AtomicInteger;

import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.ScriptException;

@SuppressWarnings("serial")
public class ScriptFunctionStub implements IScriptFunction {

    private final AtomicInteger callCount = new AtomicInteger();

    @Override
    public void call() throws ScriptException {
        callCount.incrementAndGet();
    }

    /**
     * Returns the number of times this script function was called, then resets the internal call counter.
     */
    public int consumeCallCount() {
        return callCount.getAndSet(0);
    }

}
