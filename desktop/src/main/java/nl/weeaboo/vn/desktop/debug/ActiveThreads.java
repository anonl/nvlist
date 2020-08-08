package nl.weeaboo.vn.desktop.debug;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;
import nl.weeaboo.vn.script.IScriptThread;

final class ActiveThreads implements Iterable<DebugThread> {

    private final Map<Integer, DebugThread> threadsById = new HashMap<>();

    public void updateFrom(INovel novel) {
        // Remove dead threads
        Iterator<DebugThread> itr = threadsById.values().iterator();
        while (itr.hasNext()) {
            DebugThread debugThread = itr.next();
            if (debugThread.isDead()) {
                itr.remove();
                // TODO: Generate thread dead event
            }
        }

        // Update existing threads, detect new threads
        for (IContext context : novel.getEnv().getContextManager().getContexts()) {
            Collection<? extends IScriptThread> threads = context.getScriptContext().getThreads();
            for (LuaScriptThread thread : Iterables.filter(threads, LuaScriptThread.class)) {
                int threadId = DebugThread.getThreadId(thread);
                if (!threadsById.containsKey(threadId)) {
                    threadsById.put(threadId, new DebugThread(thread));
                    // TODO: Generate new thread event
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
