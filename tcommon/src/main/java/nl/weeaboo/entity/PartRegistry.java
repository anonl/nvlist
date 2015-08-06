package nl.weeaboo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.weeaboo.collections.IntMap;

/**
 * Maintains global mapping of String names and int ids to parts.
 */
public class PartRegistry implements Serializable {

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// * Update clear method after adding/removing attributes
	// -------------------------------------------------------------------------

	private final Map<String, PartType<?>> entriesByName = new HashMap<String, PartType<?>>();
	private final IntMap<PartType<?>> entriesById = new IntMap<PartType<?>>();
	private int idGenerator;

	// -------------------------------------------------------------------------

	public PartRegistry() {
	}

	public void clear() {
		entriesByName.clear();
		entriesById.clear();
		idGenerator = 0;
	}

	private int generateId() {
		while (entriesById.containsKey(++idGenerator)) {}
		assert idGenerator >= 1;
		return idGenerator;
	}

	/**
	 * Registers a new part type in the registry.
	 *
	 * @param name The unique name for the part type.
	 * @param partClass The class which each part of this type must extend.
	 * @return An object containing information about the registered part.
	 */
	public <T extends Part> PartType<T> register(String name, Class<T> partClass) {
		if (entriesByName.containsKey(name)) {
			throw new IllegalArgumentException("Name is already in use: " + name);
		}

		int id = generateId();
		assert !entriesById.containsKey(id);

		PartType<T> entry = new PartType<T>(id, name, partClass);
		entriesByName.put(name, entry);
		entriesById.put(id, entry);
		return entry;
	}

	/**
	 * Returns information about the part registered under the given name.
	 */
	public PartType<?> get(String name) {
		return entriesByName.get(name);
	}

	/**
	 * Returns information about the part registered under the given id.
	 */
	public PartType<?> get(int id) {
		return entriesById.get(id);
	}

    public List<PartType<?>> getAll() {
        return new ArrayList<PartType<?>>(entriesByName.values());
    }

}
