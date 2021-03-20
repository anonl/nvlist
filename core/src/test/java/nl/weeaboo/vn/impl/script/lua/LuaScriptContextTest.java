package nl.weeaboo.vn.impl.script.lua;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.core.ContextStub;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.script.ThrowingScriptExceptionHandler;
import nl.weeaboo.vn.script.ScriptException;

public final class LuaScriptContextTest {

    private TestEnvironment env;
    private LuaScriptEnv scriptEnv;
    private LuaScriptContext scriptContext;

    private ContextStub context = new ContextStub();

    @Before
    public void init() throws ScriptException {
        env = TestEnvironment.newInstance();

        scriptEnv = env.getScriptEnv();
        scriptEnv.initEnv();

        scriptContext = new LuaScriptContext(scriptEnv);
    }

    @After
    public void deinit() {
        env.destroy();
    }

    /**
     * Script execution should be halted if the context becomes inactive.
     */
    @Test
    public void testContextInactiveDueToEvent() {
        // Context becomes inactive during event execution
        LuaScriptFunctionStub nextEvent = new LuaScriptFunctionStub();
        scriptContext.getEventDispatcher().addEvent(new DeactivateContext());
        scriptContext.getEventDispatcher().addEvent(nextEvent);
        updateThreads();

        // The second event isn't executed yet because the context became inactive
        Assert.assertEquals(0, nextEvent.getCallCount());

        // Once the context becomes active again, the delayed event is executed
        context.setActive(true);
        updateThreads();
        Assert.assertEquals(1, nextEvent.getCallCount());
    }

    /**
     * Script execution should be halted if the context becomes inactive.
     */
    @Test
    public void testContextInactiveDueToThread() throws ScriptException {
        LuaScriptFunctionStub nextThread = new LuaScriptFunctionStub();
        scriptContext.createThread(new DeactivateContext());
        scriptContext.createThread(nextThread);
        updateThreads();

        // The second thread isn't executed yet because the context became inactive
        Assert.assertEquals(0, nextThread.getCallCount());

        // Once the context becomes active again, the delayed thread is executed
        context.setActive(true);
        updateThreads();
        Assert.assertEquals(1, nextThread.getCallCount());
    }

    private void updateThreads() {
        scriptContext.updateThreads(context, ThrowingScriptExceptionHandler.INSTANCE);
    }

    @SuppressWarnings("serial")
    private final class DeactivateContext extends LuaScriptFunctionStub {

        @Override
        public void call() {
            context.setActive(false);
        }

    }
}
