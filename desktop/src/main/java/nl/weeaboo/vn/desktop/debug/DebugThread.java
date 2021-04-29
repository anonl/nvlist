package nl.weeaboo.vn.desktop.debug;

import java.util.Objects;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArgumentsReason;
import org.eclipse.lsp4j.debug.Thread;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

import nl.weeaboo.lua2.vm.LuaStackTraceElement;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptThread;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptThread.ILuaDebugHook;

final class DebugThread {

    private final ILuaScriptThread thread;
    private final IDebugProtocolClient peer;

    private @Nullable EStepMode stepMode;

    DebugThread(ILuaScriptThread thread, IDebugProtocolClient peer) {
        this.thread = Objects.requireNonNull(thread);
        this.peer = Objects.requireNonNull(peer);
    }

    static int getThreadId(ILuaScriptThread thread) {
        return System.identityHashCode(thread);
    }

    int getThreadId() {
        return thread.getThreadId();
    }

    ILuaScriptThread getThread() {
        return thread;
    }

    boolean isDead() {
        return thread.isDestroyed();
    }

    void pause() {
        thread.pause();

        StoppedEventArguments stopEvent = new StoppedEventArguments();
        stopEvent.setThreadId(getThreadId());
        peer.stopped(stopEvent);
    }

    void resume() {
        thread.resume();

        ContinuedEventArguments continueEvent = new ContinuedEventArguments();
        continueEvent.setThreadId(getThreadId());
        peer.continued(continueEvent);
    }

    void step(EStepMode mode) {
        stepMode = Objects.requireNonNull(mode);
        thread.resume();
    }

    private void stepHit() {
        stepMode = null;
        thread.pause();

        StoppedEventArguments stopEvent = new StoppedEventArguments();
        stopEvent.setThreadId(getThreadId());
        stopEvent.setReason(StoppedEventArgumentsReason.STEP);
        peer.stopped(stopEvent);
    }

    /**
     * Creates a debug adapter protocol (DAP) thread object based on this thread.
     */
    Thread toDapThread() {
        Thread result = new org.eclipse.lsp4j.debug.Thread();
        result.setId(getThreadId());
        result.setName(thread.getName());
        return result;
    }

    StackFrame[] getStackTrace() {
        return thread.stackTrace().stream().map(this::toStackFrame).toArray(StackFrame[]::new);
    }

    void installHook(Breakpoints breakpoints) {
        thread.installHook(new DebugHook(breakpoints));
    }

    @Override
    public String toString() {
        return thread.toString();
    }

    private StackFrame toStackFrame(LuaStackTraceElement elem) {
        String relativePath = elem.getFileName();

        Source source = new Source();
        source.setName(relativePath);
        source.setPath(NameMapping.toAbsoluteScriptPath(relativePath));

        StackFrame sf = new StackFrame();
        sf.setName(elem.getFunctionName());
        sf.setSource(source);
        sf.setLine(elem.getLineNumber());
        return sf;
    }

    private final class DebugHook implements ILuaDebugHook {

        private final Breakpoints breakpoints;
        private int depth = 0;

        public DebugHook(Breakpoints breakpoints) {
            this.breakpoints = breakpoints;
        }

        @Override
        public void onEvent(String eventName, int lineNumber) {
            if (stepMode != null) {
                switch (stepMode) {
                case IN:
                    if (eventName.endsWith("call")) {
                        stepHit();
                    }
                    break;
                case OUT:
                    if (eventName.equals("return")) {
                        // Pausing during a "tail return" seems to cause stack corruption (this is a bug)
                        stepHit();
                    }
                    break;
                case NEXT:
                    // Ignore line events in nested function calls
                    if (eventName.endsWith("call")) {
                        depth++;
                    } else if (eventName.endsWith("return")) {
                        depth--;
                    }

                    if (depth <= 0 && "line".equals(eventName)) {
                        stepHit();
                    }
                    break;
                }
            } else {
                LuaStackTraceElement lsf = thread.stackTraceElem(0);
                if (lsf != null) {
                    StackFrame sf = toStackFrame(lsf);
                    if (breakpoints.shouldPause(sf)) {
                        pause();
                    }
                }
            }
        }

    }
}