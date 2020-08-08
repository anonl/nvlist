package nl.weeaboo.vn.desktop.debug;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.debug.ThreadEventArguments;
import org.eclipse.lsp4j.debug.ThreadEventArgumentsReason;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

import com.google.common.collect.Iterables;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;
import nl.weeaboo.vn.script.IScriptThread;

final class ActiveThreads implements Iterable<DebugThread> {

    private final Breakpoints breakpoints;
    private final Map<Integer, DebugThread> threadsById = new HashMap<>();

    public ActiveThreads(Breakpoints breakpoints) {
        this.breakpoints = Objects.requireNonNull(breakpoints);
    }

    public void update(INovel novel, IDebugProtocolClient peer) {
        // Remove dead threads
        Iterator<DebugThread> itr = threadsById.values().iterator();
        while (itr.hasNext()) {
            DebugThread debugThread = itr.next();
            if (debugThread.isDead()) {
                itr.remove();

                ThreadEventArguments threadEvent = new ThreadEventArguments();
                threadEvent.setThreadId(debugThread.getThreadId());
                threadEvent.setReason(ThreadEventArgumentsReason.EXITED);
                peer.thread(threadEvent);
            }
        }

        // Update existing threads, detect new threads
        for (IContext context : novel.getEnv().getContextManager().getContexts()) {
            Collection<? extends IScriptThread> threads = context.getScriptContext().getThreads();
            for (LuaScriptThread thread : Iterables.filter(threads, LuaScriptThread.class)) {
                int threadId = DebugThread.getThreadId(thread);
                if (!threadsById.containsKey(threadId)) {
                    DebugThread debugThread = new DebugThread(thread, peer);
                    threadsById.put(threadId, debugThread);

                    debugThread.installHook(breakpoints);

                    ThreadEventArguments threadEvent = new ThreadEventArguments();
                    threadEvent.setThreadId(debugThread.getThreadId());
                    threadEvent.setReason(ThreadEventArgumentsReason.STARTED);
                    peer.thread(threadEvent);
                }
            }
        }
    }

    public @Nullable DebugThread findById(int threadId) {
        return threadsById.get(threadId);
    }

    @Override
    public Iterator<DebugThread> iterator() {
        return Collections.unmodifiableCollection(threadsById.values()).iterator();
    }

}
