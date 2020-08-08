package nl.weeaboo.vn.desktop.debug;

import java.util.Objects;

import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.Thread;

import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;

final class DebugThread {

    private final int threadId;
    private final LuaScriptThread thread;

    DebugThread(LuaScriptThread thread) {
        this.threadId = getThreadId(thread);
        this.thread = Objects.requireNonNull(thread);
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

    public void pause() {
        thread.pause();
    }

    public void unpause() {
        thread.unpause();
    }

    /**
     * Creates a debug adapter protocol (DAP) thread object based on this thread.
     */
    public Thread toDapThread() {
        Thread result = new org.eclipse.lsp4j.debug.Thread();
        result.setId(threadId);
        result.setName(thread.getName());
        return result;
    }

    public StackFrame[] getStackTrace() {
        return thread.getStackTrace().stream().map(line -> {
            StackFrame sf = new StackFrame();
            sf.setName(line);
            return sf;
        }).toArray(StackFrame[]::new);
    }

}