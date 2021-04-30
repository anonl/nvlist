package nl.weeaboo.vn.desktop.debug;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.Thread;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.lua2.vm.LuaStackTraceElement;
import nl.weeaboo.vn.impl.script.lua.ILuaDebugHook;
import nl.weeaboo.vn.impl.script.lua.LuaDebugEvent;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThreadMock;

public final class DebugThreadTest {

    private final LuaScriptThreadMock luaThread = new LuaScriptThreadMock() {

        private static final long serialVersionUID = 1L;

        @Override
        public List<LuaStackTraceElement> stackTrace() {
            return ImmutableList.of(
                    new LuaStackTraceElement("a", 111, "a.func"),
                    new LuaStackTraceElement("b", 222, "b.func")
            );
        }
    };

    private final DebugProtocolClientMock dpc = new DebugProtocolClientMock();
    private DebugThread debugThread;

    @Before
    public void before() {
        debugThread = new DebugThread(luaThread, dpc);
    }

    @Test
    public void testPauseResume() {
        debugThread.pause();
        Assert.assertEquals(true, luaThread.isPaused());

        StoppedEventArguments stopped = dpc.consumeStopped();
        Assert.assertEquals(luaThread.getThreadId(), (int)stopped.getThreadId());

        debugThread.resume();
        Assert.assertEquals(false, luaThread.isPaused());

        ContinuedEventArguments cont = dpc.consumeContinued();
        Assert.assertEquals(luaThread.getThreadId(), cont.getThreadId());
    }

    @Test
    public void testStackTrace() {
        Assert.assertArrayEquals(new StackFrame[] {
                stackFrame("a", 111, "a.func"),
                stackFrame("b", 222, "b.func"),
        }, debugThread.getStackTrace());
    }

    @Test
    public void testToDapThread() {
        Thread dapThread = debugThread.toDapThread();
        Assert.assertEquals(luaThread.getThreadId(), dapThread.getId());
        Assert.assertEquals(luaThread.getName(), dapThread.getName());
    }

    @Test
    public void testDebugHook() {
        Breakpoints breakpoints = new Breakpoints();
        breakpoints.setBreakpoints(source("a"), new SourceBreakpoint[] { sourceBreakpoint(111) });
        debugThread.installHook(breakpoints);
        Assert.assertEquals(false, luaThread.isPaused());

        ILuaDebugHook hook = luaThread.getDebugHook();

        // This event triggers the breakpoint (based on the thread's stacktrace)
        hook.onEvent(LuaDebugEvent.LINE, 111);
        Assert.assertEquals(true, luaThread.isPaused());
        Assert.assertNotNull(dpc.consumeStopped());

        debugThread.step(EStepMode.IN);
        assertAssertSteppingStoppedBy(hook, EnumSet.of(LuaDebugEvent.CALL));

        debugThread.step(EStepMode.OUT);
        assertAssertSteppingStoppedBy(hook, EnumSet.of(LuaDebugEvent.RETURN, LuaDebugEvent.TAIL_RETURN));

        // Next waits until the next line, ignoring line events in nested function calls
        debugThread.step(EStepMode.NEXT);
        hook.onEvent(LuaDebugEvent.CALL, 0);
        hook.onEvent(LuaDebugEvent.LINE, 0); // Ignored
        hook.onEvent(LuaDebugEvent.RETURN, 0);
        Assert.assertEquals(false, luaThread.isPaused()); // Still stepping
        hook.onEvent(LuaDebugEvent.LINE, 0);
        Assert.assertEquals(true, luaThread.isPaused()); // Stepping finished
        Assert.assertNotNull(dpc.consumeStopped());
    }

    @Test
    public void testDelegateFunctions() {
        Assert.assertEquals(luaThread, debugThread.getThread());
        Assert.assertEquals(luaThread.isDestroyed(), debugThread.isDead());
        Assert.assertEquals(luaThread.toString(), debugThread.toString());
    }

    private void assertAssertSteppingStoppedBy(ILuaDebugHook hook, EnumSet<LuaDebugEvent> events) {
        Assert.assertNull(dpc.consumeStopped());

        for (LuaDebugEvent event : EnumSet.complementOf(events)) {
            hook.onEvent(event, 0);
            Assert.assertEquals(false, luaThread.isPaused()); // Still stepping
        }
        for (LuaDebugEvent event : events) {
            hook.onEvent(event, 0);
            Assert.assertEquals(true, luaThread.isPaused()); // Stepping finished
            Assert.assertNotNull(dpc.consumeStopped());

            luaThread.resume();
        }
    }

    private static StackFrame stackFrame(String file, int line, String func) {
        StackFrame sf = new StackFrame();
        sf.setSource(source(file));
        sf.setName(func);
        sf.setLine(line);
        return sf;
    }

    private static Source source(String file) {
        Source source = new Source();
        source.setName(file);
        source.setPath(NameMapping.toAbsoluteScriptPath(file));
        return source;
    }

    private static SourceBreakpoint sourceBreakpoint(int line) {
        SourceBreakpoint sb = new SourceBreakpoint();
        sb.setLine(line);
        return sb;
    }
}
