package nl.weeaboo.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Maps ints to Objects.
 */
public final class IntMap<V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Object REMOVED = new Object();

	private int[] keys; // Sorted array of keys
	private Object[] values;
	private int length; // First $length slots of keys are filled
	private int size; // The number of non-removed slots in $values

	private boolean containsGaps;

	public IntMap() {
		this(8);
	}

	public IntMap(int initialCapacity) {
		keys = new int[initialCapacity];
		values = new Object[initialCapacity];
	}

	// Functions
	private void resize(int capacity) {
		int[] newKeys = new int[capacity];
		Object[] newVals = new Object[capacity];
		
		System.arraycopy(keys, 0, newKeys, 0, length);
		System.arraycopy(values, 0, newVals, 0, length);
		
		keys = newKeys;
		values = newVals;
	}

	public V remove(int key) {
		int i = Arrays.binarySearch(keys, 0, length, key);
		if (i >= 0 && values[i] != REMOVED) {
			@SuppressWarnings("unchecked")
			V oldval = (V)values[i];			
			values[i] = REMOVED;
			containsGaps = true;			
			size--;
			return oldval;
		}
		return null;
	}

	private void compact() {
		int removed = 0;
		for (int n = 0; n < length; n++) {
			Object val = values[n];
			if (val == REMOVED) {
				removed++;
			} else if (removed > 0) {
				keys[n - removed] = keys[n];
				values[n - removed] = val;
			}
		}
		for (int n = length - removed; n < length; n++) {
			values[n] = null;
		}
		containsGaps = false;
		length -= removed;
	}

	public void clear() {
		Arrays.fill(values, 0, length, null);
		containsGaps = false;
		length = 0;
		size = 0;
	}
	
	public Iterable<V> values() {
		return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					int cursor = -1;
					
					@Override
					public boolean hasNext() {
						return cursor + 1 < size;
					}

					@Override
					public V next() {
						return valueAt(++cursor);
					}

					@Override
					public void remove() {
						removeAt(cursor);
					}					
				};
			}
		};
	}
	
	// Getters
	public int[] getKeys() {
		if (containsGaps) {
			compact();
		}
		
		int[] result = new int[length];
		System.arraycopy(keys, 0, result, 0, length);
		return result;
	}
	
	public boolean containsKey(int key) {
		int i = Arrays.binarySearch(keys, 0, length, key);
		return i >= 0 && values[i] != REMOVED;
	}
	
	public V get(int key) {
		int i = Arrays.binarySearch(keys, 0, length, key);
		if (i < 0 || values[i] == REMOVED) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		V val = (V) values[i];
		return val;
	}

	public boolean isEmpty() {
		return size() == 0;
	}
	
	public int size() {
		return size;
	}
	
	public int keyAt(int index) {
		if (containsGaps) {
			compact();
		}
		if (index >= length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return keys[index];
	}
	
	public V valueAt(int index) {
		if (containsGaps) {
			compact();
		}
		if (index >= length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		Object raw = values[index];
		if (raw == REMOVED) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		V val = (V) values[index];
		return val;
	}

	// Setters
	public void putAll(Map<Integer, ? extends V> map) {
		for (Entry<Integer, ? extends V> entry : map.entrySet()) {
			int key = entry.getKey().intValue();
			put(key, entry.getValue());
		}
	}
	
	public void putAll(IntMap<? extends V> map) {
		for (int n = 0; n < map.length; n++) {
			if (map.values[n] != REMOVED) {
				@SuppressWarnings("unchecked")
				V val = (V)map.values[n];
				put(map.keys[n], val);
			}
		}
	}
	
	public V put(int key, V val) {
		int i = Arrays.binarySearch(keys, 0, length, key);
		if (i >= 0) {
			// Key already exists, overwrite exiting value			
			@SuppressWarnings("unchecked")
			V oldval = (V)values[i];
			if (oldval == REMOVED) {
				oldval = null;
				size++;
			}
			values[i] = val;
			return oldval;
		}

		// Find insertion index
		i = -(i + 1);

		if (i < length && values[i] == REMOVED) {
			// Key doesn't exist, but the value following it has been deleted so
			// we can use its slot
			keys[i] = key;
			values[i] = val;
			size++;
			return null; //Old value was a dummy
		}

		if (containsGaps && length >= keys.length) {
			// Array is full, but contains gaps so try to compact in order to
			// make room
			compact();

			// Update i for the new situation
			i = Arrays.binarySearch(keys, 0, length, key);
			i = -(i + 1);
		}

		if (length >= keys.length) {
			// We need more room for the new index
			resize(length + 16);
		}

		if (i < length) {
			// Move the existing entries (following out insertion point) one
			// position further down the list to make room
			System.arraycopy(keys, i, keys, i + 1, length - i);
			System.arraycopy(values, i, values, i + 1, length - i);
		}

		keys[i] = key;
		values[i] = val;
		size++;
		length++;
		return null; // Old value doesn't exist
	}
	
	public boolean removeAt(int index) {
		if (containsGaps) {
			compact();
		}
		if (index >= length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		Object oldval = values[index];
		if (oldval == REMOVED) {
			return false;
		}
		
		values[index] = REMOVED;
		containsGaps = true;
		size--;
		return true;
	}

	public V putAtIndex(int index, V val) {
		if (containsGaps) {
			compact();
		}
		if (index >= length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		@SuppressWarnings("unchecked")
		V oldval = (V)values[index];
		if (oldval == REMOVED) {
			oldval = null;
			size++;
		}
		values[index] = val;
		return oldval;
	}
	
}
