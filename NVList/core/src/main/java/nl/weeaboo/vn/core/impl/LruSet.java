package nl.weeaboo.vn.core.impl;

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

		this.backing = new LinkedHashSet<T>();
		this.maxSize = maxSize;
	}

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
		for (int n = 0; n < toRemove; n++) {
			itr.remove();
		}
	}

	public boolean contains(Object obj) {
		return backing.contains(obj);
	}

	public boolean remove(Object obj) {
		return backing.remove(obj);
	}

	public void clear() {
		backing.clear();
	}

}
