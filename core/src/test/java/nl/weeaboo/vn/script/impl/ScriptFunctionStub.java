package nl.weeaboo.vn.script.impl;

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

    public int consumeCallCount() {
        return callCount.getAndSet(0);
    }

}
