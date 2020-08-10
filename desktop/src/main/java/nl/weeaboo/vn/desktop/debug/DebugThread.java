package nl.weeaboo.vn.desktop.debug;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArgumentsReason;
import org.eclipse.lsp4j.debug.Thread;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;

import nl.weeaboo.lua2.stdlib.DebugTrace;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread.ILuaDebugHook;

final class DebugThread {

    private final int threadId;
    private final LuaScriptThread thread;
    private final IDebugProtocolClient peer;

    private @Nullable EStepMode stepMode;

    DebugThread(LuaScriptThread thread, IDebugProtocolClient peer) {
        this.threadId = getThreadId(thread);
        this.thread = Objects.requireNonNull(thread);
        this.peer = Objects.requireNonNull(peer);
    }

    static int getThreadId(LuaScriptThread thread) {
        return System.identityHashCode(thread);
    }

    int getThreadId() {
        return threadId;
    }

    LuaScriptThread getThread() {
        return thread;
    }

    boolean isDead() {
        return thread.isDestroyed();
    }

    void pause() {
        thread.pause();

        StoppedEventArguments stopEvent = new StoppedEventArguments();
        stopEvent.setThreadId(threadId);
        peer.stopped(stopEvent);
    }

    void unpause() {
        thread.unpause();

        ContinuedEventArguments continueEvent = new ContinuedEventArguments();
        continueEvent.setThreadId(threadId);
        peer.continued(continueEvent);
    }

    void step(EStepMode mode) {
        stepMode = Objects.requireNonNull(mode);
        thread.unpause();
    }

    private void stepHit() {
        stepMode = null;
        thread.pause();

        StoppedEventArguments stopEvent = new StoppedEventArguments();
        stopEvent.setThreadId(threadId);
        stopEvent.setReason(StoppedEventArgumentsReason.STEP);
        peer.stopped(stopEvent);
    }

    /**
     * Creates a debug adapter protocol (DAP) thread object based on this thread.
     */
    Thread toDapThread() {
        Thread result = new org.eclipse.lsp4j.debug.Thread();
        result.setId(threadId);
        result.setName(thread.getName());
        return result;
    }

    StackFrame[] getStackTrace() {
        return thread.getStackTrace().stream().map(this::toStackFrame).toArray(StackFrame[]::new);
    }

    void installHook(Breakpoints breakpoints) {
        thread.installHook(new DebugHook(breakpoints));
    }

    @Override
    public String toString() {
        return thread.toString();
    }

    private StackFrame toStackFrame(String fileline) {
        String relativePath = fileline;
        int lineNumber = 0;

        List<String> parts = Splitter.on(':').splitToList(fileline);
        if (parts.size() == 2) {
            relativePath = parts.get(0);
            lineNumber = MoreObjects.firstNonNull(Ints.tryParse(parts.get(1)), 0);
        }

        Source source = new Source();
        source.setName(relativePath);
        source.setPath(NameMapping.toAbsoluteScriptPath(relativePath));

        StackFrame sf = new StackFrame();
        sf.setName(fileline);
        sf.setSource(source);
        sf.setLine(lineNumber);
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
            StackFrame sf = toStackFrame(DebugTrace.fileline());
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
            } else if (breakpoints.shouldPause(sf)) {
                pause();
            }
        }

    }
}