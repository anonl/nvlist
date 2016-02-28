package nl.weeaboo.vn.core.impl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Sets;

import nl.weeaboo.vn.core.IEnvironment;

class SystemModuleMock extends SystemModule {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger doExitCalled = new AtomicInteger();
    private final AtomicReference<String> lastCalled = new AtomicReference<String>();
    private final Set<String> existingFunctions = Sets.newHashSet();

    public SystemModuleMock(IEnvironment env) {
        super(env);
    }

    public void addFunction(String functionName) {
        existingFunctions.add(functionName);
    }

    public void removeFunction(String functionName) {
        existingFunctions.remove(functionName);
    }

    @Override
    protected void doExit() {
        doExitCalled.incrementAndGet();
        super.doExit();
    }

    public int consumeDoExitCount() {
        return doExitCalled.getAndSet(0);
    }

    @Override
    protected boolean call(String functionName) {
        lastCalled.set(functionName);
        return existingFunctions.contains(functionName);
    }

    public String consumeLastCallled() {
        return lastCalled.getAndSet(null);
    }

}
