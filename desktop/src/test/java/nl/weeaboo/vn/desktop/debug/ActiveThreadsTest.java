package nl.weeaboo.vn.desktop.debug;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.ThreadEventArguments;
import org.eclipse.lsp4j.debug.ThreadEventArgumentsReason;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import nl.weeaboo.vn.impl.core.ContextManagerStub;
import nl.weeaboo.vn.impl.core.ContextStub;
import nl.weeaboo.vn.impl.script.ScriptContextStub;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptThread;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThreadMock;
import nl.weeaboo.vn.script.IScriptThread;

public final class ActiveThreadsTest {

    private final ContextManagerStub contextManager = new ContextManagerStub();
    private final DebugProtocolClientMock dpc = new DebugProtocolClientMock();

    private ActiveThreads activeThreads;

    @Before
    public void before() {
        Breakpoints breakpoints = new Breakpoints();
        breakpoints.setBreakpoints(DapTestHelper.source("a"), new SourceBreakpoint[] {
                DapTestHelper.sourceBreakpoint(111)
        });
        activeThreads = new ActiveThreads(breakpoints);
    }

    @Test
    public void testThreadStartStop() {
        ILuaScriptThread thread = new LuaScriptThreadMock();
        contextManager.addContext(newContext(thread));

        update();
        ThreadEventArguments startEvent = dpc.consumeThread();
        Assert.assertEquals(thread.getThreadId(), startEvent.getThreadId());
        Assert.assertEquals(ThreadEventArgumentsReason.STARTED, startEvent.getReason());

        thread.destroy();

        update();
        ThreadEventArguments exitEvent = dpc.consumeThread();
        Assert.assertEquals(thread.getThreadId(), exitEvent.getThreadId());
        Assert.assertEquals(ThreadEventArgumentsReason.EXITED, exitEvent.getReason());
    }

    @Test
    public void testFindThread() {
        ILuaScriptThread threadA = new LuaScriptThreadMock();
        contextManager.addContext(newContext(threadA));

        ILuaScriptThread threadB = new LuaScriptThreadMock();
        contextManager.addContext(newContext(threadB));

        update();

        Assert.assertEquals(2, Iterables.size(activeThreads));
        assertThreadEquals(threadA, activeThreads.getPrimaryThread());

        assertThreadEquals(threadA, activeThreads.findByFrameId(null));
        assertThreadEquals(null, activeThreads.findByFrameId(1));

        assertThreadEquals(threadA, activeThreads.findById(threadA.getThreadId()));
        assertThreadEquals(threadB, activeThreads.findById(threadB.getThreadId()));
    }

    private void update() {
        activeThreads.update(contextManager, dpc);
    }

    private ContextStub newContext(IScriptThread thread) {
        ContextStub context = new ContextStub();
        context.setScriptContext(new ScriptContextStub(thread));
        return context;
    }

    private void assertThreadEquals(@Nullable ILuaScriptThread expected, @Nullable DebugThread actual) {
        if (actual == null) {
            Assert.assertNull(expected);
        } else {
            Assert.assertNotNull(expected);
            Assert.assertEquals(expected.getThreadId(), actual.getThreadId());
        }
    }
}
