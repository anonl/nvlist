package nl.weeaboo.vn.impl.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.lua2.io.DelayedReader;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.vn.core.IDestructible;

/** List implementation that automatically removes destroyed elements */
public final class DestructibleElemList<T extends IDestructible> extends AbstractList<T> implements Externalizable {

    // --- Note: Uses custom serialization ---
    private final List<T> elements = Lists.newArrayList();
    // --- Note: Uses custom serialization ---

    private transient @Nullable ImmutableCollection<T> cachedSnapshot;

    /** A no-arg public constructor is required by the {@link Externalizable} interface */
    public DestructibleElemList() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        LuaSerializer ls = LuaSerializer.getCurrent();

        out.writeInt(elements.size());
        for (T elem : elements) {
            if (ls != null) {
                ls.writeDelayed(elem);
            } else {
                out.writeObject(elem);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        LuaSerializer ls = LuaSerializer.getCurrent();
        elements.clear();

        int length = in.readInt();
        for (int n = 0; n < length; n++) {
            if (ls != null) {
                ls.readDelayed(new DelayedReader() {
                    @Override
                    public void onRead(Object obj) {
                        elements.add((T)obj);
                    }
                });
            } else {
                elements.add((T)in.readObject());
            }
        }
    }

    @Override
    public void add(int index, T elem) {
        if (elem.isDestroyed()) {
            return;
        }

        elements.add(index, elem);
        invalidateCachedSnapshot();
    }

    @Override
    public T get(int index) {
        return elements.get(index);
    }

    @Override
    public T set(int index, T element) {
        if (element == null) {
            throw new NullPointerException();
        }

        T replaced = elements.set(index, element);
        invalidateCachedSnapshot();
        return replaced;
    }

    @Override
    public boolean remove(Object elem) {
        boolean removed = elements.remove(elem);
        invalidateCachedSnapshot();
        return removed;
    }

    @Override
    public T remove(int index) {
        T removed = elements.remove(index);
        invalidateCachedSnapshot();
        return removed;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = elements.removeAll(c);
        invalidateCachedSnapshot();
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean removed = elements.retainAll(c);
        invalidateCachedSnapshot();
        return removed;
    }

    /** Removes all elements from the list. */
    @Override
    public void clear() {
        elements.clear();
        invalidateCachedSnapshot();
    }

    /** Destroys all elements, then clears the list. */
    public void destroyAll() {
        for (T elem : getSnapshot()) {
            elem.destroy();
        }

        // We can't use clear, because destroying elements may result in new elements being added to this list
        removeDestroyedElements();
    }

    /**
     * @return {@code true} if the given element is stored one or more times in this list.
     */
    @Override
    public boolean contains(Object elem) {
        removeDestroyedElements();
        return elements.contains(elem);
    }

    /**
     * @return The number of non-destroyed elements in the list.
     */
    @Override
    public int size() {
        removeDestroyedElements();
        return elements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getSnapshot().asList().listIterator(index);
    }

    /**
     * Returns a read-only snapshot of the elements in the list.
     */
    public ImmutableCollection<T> getSnapshot() {
        ImmutableCollection<T> result = cachedSnapshot;
        if (result == null) {
            result = getSnapshot(Predicates.alwaysTrue());
            cachedSnapshot = result;
        }
        return result;
    }

    /**
     * Returns a read-only snapshot of the elements in the list.
     * @param predicate Only include elements that pass the predicate.
     */
    public ImmutableCollection<T> getSnapshot(Predicate<? super T> predicate) {
        removeDestroyedElements();

        ImmutableList.Builder<T> result = ImmutableList.builder();
        for (T elem : elements) {
            if (predicate.apply(elem)) {
                result.add(elem);
            }
        }
        return result.build();
    }

    /**
     * Returns the first non-destroyed element matching the given predicate, or {@code null} if not found.
     */
    public @Nullable T findFirst(Predicate<? super T> predicate) {
        for (T elem : elements) {
            if (!elem.isDestroyed() && predicate.apply(elem)) {
                return elem;
            }
        }
        return null;
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
            invalidateCachedSnapshot();
        }
    }

    private void invalidateCachedSnapshot() {
        cachedSnapshot = null;
    }

}
