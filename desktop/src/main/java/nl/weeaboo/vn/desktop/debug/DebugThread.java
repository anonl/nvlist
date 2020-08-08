package nl.weeaboo.vn.desktop.debug;

import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.Thread;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;

import nl.weeaboo.lua2.stdlib.DebugTrace;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;

final class DebugThread {

    private final int threadId;
    private final LuaScriptThread thread;
    private final IDebugProtocolClient peer;

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
        return thread.getStackTrace().stream().map(line -> {
            StackFrame sf = new StackFrame();
            sf.setName(line);
            return sf;
        }).toArray(StackFrame[]::new);
    }

    void installHook(Breakpoints breakpoints) {
        thread.installHook(() -> {
            List<String> parts = Splitter.on(':').splitToList(DebugTrace.fileline());
            if (parts.size() == 2) {
                String path = parts.get(0);
                int line = MoreObjects.firstNonNull(Ints.tryParse(parts.get(1)), 0);
                if (breakpoints.shouldPause(path, line)) {
                    pause();
                }
            }
        });
    }

}