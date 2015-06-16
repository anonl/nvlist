package nl.weeaboo.vn.script.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.vn.script.IScriptThread;

public class ScriptThreadCollection<T extends IScriptThread> implements Serializable, Iterable<T> {

    private static final long serialVersionUID = 1L;

    private final List<T> threads = new CopyOnWriteArrayList<T>();

    public void add(T thread) {
        threads.add(thread);
    }

    public void destroyThreads() {
        for (IScriptThread thread : threads) {
            thread.destroy();
        }
        threads.clear();
    }

    private void cleanupDeadThreads() {
        List<T> cleanup = null;
        for (T thread : threads) {
            if (thread.isDestroyed()) {
                if (cleanup == null) {
                    cleanup = new ArrayList<T>();
                }
                cleanup.add(thread);
            }
        }
        if (cleanup != null) {
            threads.removeAll(cleanup);
        }
    }

    public Collection<T> getThreads() {
        cleanupDeadThreads();
        return Collections.unmodifiableCollection(threads);
    }

    @Override
    public Iterator<T> iterator() {
        cleanupDeadThreads();
        return threads.iterator();
    }

}
