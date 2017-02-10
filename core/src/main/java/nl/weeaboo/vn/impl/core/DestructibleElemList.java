package nl.weeaboo.vn.impl.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.lua2.io.DelayedReader;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.vn.core.IDestructible;

/** List implementation that automatically removes destroyed elements */
public final class DestructibleElemList<T extends IDestructible> implements Iterable<T>, Externalizable {

    private static final long serialVersionUID = 1L;

    // --- Note: Uses custom serialization ---
    private final List<T> elements = Lists.newArrayList();
    // --- Note: Uses custom serialization ---

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

    /** Adds an element to the list. */
    public void add(T elem) {
        elements.add(elem);
    }

    /** Removes an element from the list. */
    public void remove(Object elem) {
        elements.remove(elem);
    }

    /** Removes all elements from the list. */
    public void clear() {
        elements.clear();
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
    public boolean contains(Object elem) {
        removeDestroyedElements();
        return elements.contains(elem);
    }

    /**
     * @return The number of non-destroyed elements in the list.
     */
    public int size() {
        removeDestroyedElements();
        return elements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    /**
     * Returns a read-only snapshot of the elements in the list.
     */
    public Collection<T> getSnapshot() {
        return getSnapshot(Predicates.alwaysTrue());
    }

    /**
     * Returns a read-only snapshot of the elements in the list.
     * @param predicate Only include elements that pass the predicate.
     */
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
