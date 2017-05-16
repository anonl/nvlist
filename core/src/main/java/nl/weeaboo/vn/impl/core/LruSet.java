package nl.weeaboo.vn.impl.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;

import nl.weeaboo.common.Checks;

public final class LruSet<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LinkedHashSet<T> backing;
    private final int maxSize;

    public LruSet(int maxSize) {
        Checks.checkRange(maxSize, "maxSize", 1);

        this.backing = new LinkedHashSet<>();
        this.maxSize = maxSize;
    }

    /**
     * Adds an item to the set.
     *
     * @return {@code true} if the item was newly added to the set, {@code false} if the item was already contained in
     *         the set.
     */
    public boolean add(T e) {
        boolean alreadyContained = backing.remove(e);
        if (alreadyContained) {
            backing.add(e);
            return false;
        }

        reserveSize(1);
        return backing.add(e);
    }

    private void reserveSize(int freeSlots) {
        Checks.checkRange(freeSlots, "freeSlots", 0, maxSize);

        int toRemove = (backing.size() + freeSlots) - maxSize;

        Iterator<T> itr = backing.iterator();
        for (int n = 0; n < toRemove && itr.hasNext(); n++) {
            itr.next();
            itr.remove();
        }
    }

    /**
     * @return {@code true} if the given object is contained in this set.
     */
    public boolean contains(Object obj) {
        return backing.contains(obj);
    }

    /**
     * Attempts to remove the given object from the set.
     *
     * @return {@code true} if the object was removed from this set, or {@code false} if this set didn't contain the
     *         given object.
     */
    public boolean remove(Object obj) {
        return backing.remove(obj);
    }

    /**
     * Removes all objects from the set.
     */
    public void clear() {
        backing.clear();
    }

}
