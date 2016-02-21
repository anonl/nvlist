package nl.weeaboo.vn.core.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.vn.core.IDestructible;

/** List implementation that automatically removes destroyed elements */
public class DestructibleElemList<T extends IDestructible> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> elements = Lists.newArrayList();

    public void add(T elem) {
        elements.add(elem);
    }

    public void remove(Object elem) {
        elements.remove(elem);
    }

    public void clear() {
        elements.clear();
    }

    public void destroyAll() {
        for (T elem : getSnapshot()) {
            elem.destroy();
        }

        // We can't use clear, because destroying elements may result in new elements being added to this list
        removeDestroyedElements();
    }

    public boolean contains(Object elem) {
        removeDestroyedElements();
        return elements.contains(elem);
    }

    public int size() {
        removeDestroyedElements();
        return elements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    public Collection<T> getSnapshot() {
        return getSnapshot(Predicates.alwaysTrue());
    }

    public Collection<T> getSnapshot(Predicate<? super T> predicate) {
        removeDestroyedElements();

        ImmutableList.Builder<T> result = ImmutableList.builder();
        for (T elem : elements) {
            if (predicate.apply(elem)) {
                result.add(elem);
            }
        }
        return result.build();
    }

    private void removeDestroyedElements() {
        // Determine which elements are destroyed
        List<T> removed = null;
        for (T elem : elements) {
            if (elem.isDestroyed()) {
                if (removed == null) {
                    removed = Lists.newArrayList();
                }
                removed.add(elem);
            }
        }

        // Remove destroyed elements
        if (removed != null) {
            elements.removeAll(removed);
        }
    }

}
