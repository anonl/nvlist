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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;

final class ActiveThreads implements Iterable<DebugThread> {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveThreads.class);

    private final Breakpoints breakpoints;
    private final Map<Integer, DebugThread> threadsById = new HashMap<>();

    private @Nullable DebugThread primaryThread;

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

                LOG.debug("Send thread stop event to debug adapter client (now {} active threads)",
                        threadsById.size());
                ThreadEventArguments threadEvent = new ThreadEventArguments();
                threadEvent.setThreadId(debugThread.getThreadId());
                threadEvent.setReason(ThreadEventArgumentsReason.EXITED);
                peer.thread(threadEvent);
            }
        }

        // Update existing threads, detect new threads
        IContextManager contextManager = novel.getEnv().getContextManager();
        IContext primaryContext = contextManager.getPrimaryContext();
        for (IContext context : contextManager.getContexts()) {
            IScriptContext scriptContext = context.getScriptContext();
            Collection<? extends IScriptThread> threads = scriptContext.getThreads();
            for (LuaScriptThread thread : Iterables.filter(threads, LuaScriptThread.class)) {
                int threadId = DebugThread.getThreadId(thread);

                DebugThread debugThread = threadsById.get(threadId);
                if (debugThread == null) {
                    debugThread = new DebugThread(thread, peer);
                    threadsById.put(threadId, debugThread);

                    debugThread.installHook(breakpoints);

                    LOG.debug("Send thread start event to debug adapter client (now {} active threads)",
                            threadsById.size());
                    ThreadEventArguments threadEvent = new ThreadEventArguments();
                    threadEvent.setThreadId(debugThread.getThreadId());
                    threadEvent.setReason(ThreadEventArgumentsReason.STARTED);
                    peer.thread(threadEvent);
                }

                if (context == primaryContext && thread == scriptContext.getMainThread()) {
                    primaryThread = debugThread;
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

    public @Nullable DebugThread findByFrameId(@Nullable Integer frameId) {
        if (frameId == null) {
            return primaryThread;
        } else {
            return primaryThread;
        }
    }

}
