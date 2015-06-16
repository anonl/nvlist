package nl.weeaboo.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.collections.IntMap;

/**
 * Helper object for Scene to keep track of its entities.
 */
final class EntityManager {

	// -------------------------------------------------------------------------
	// * Attributes must be serialized manually
	// * Update reset method after adding/removing attributes
	// -------------------------------------------------------------------------
	private Scene scene;
	private final IntMap<Entity> entities = new IntMap<Entity>();
	private final Map<EntityStreamDef, EntityStream> streamMap = new HashMap<EntityStreamDef, EntityStream>();
	private int idGenerator;
	// -------------------------------------------------------------------------

	public EntityManager(Scene s) {
		this.scene = s;
	}

	void reset() {
		entities.clear();
		streamMap.clear();
		idGenerator = 0;
	}

	void serialize(ObjectOutput out) throws IOException {
		out.writeInt(entities.size());
		for (int n = 0; n < entities.size(); n++) {
			Entity e = entities.valueAt(n);
			e.serialize(out);
		}

		// The contents of streamMap are not serialized
	}

	void deserialize(Scene s, ObjectInput in) throws IOException, ClassNotFoundException {
		reset();

		scene = s;

		int entitiesL = in.readInt();
		for (int n = 0; n < entitiesL; n++) {
			Entity e = new Entity(s, 0);
			e.deserialize(s, in);
		}
	}

	int generateId() {
		while (entities.containsKey(++idGenerator)) {}
		return idGenerator;
	}

	public void invalidateStreams() {
		for (EntityStream stream : streamMap.values()) {
			stream.invalidate();
		}
	}

	private EntityStream newEntityStream(EntityStreamDef esd) {
		EntityStream stream = new EntityStream(esd);
		stream.setSource(entities.values());
		return stream;
	}

	@Override
	public String toString() {
		return "EntityManager[" + scene + "]";
	}

	public void add(Entity e) {
		Entity old = entities.put(e.getId(), e);
		if (e != old) {
			if (old != null) {
				throw new IllegalStateException("New entity replaced a different entity with the same ID (" + e.getId() + "). This should not be possible.");
			}
			invalidateStreams();
		}
	}

	public boolean remove(Entity e) {
		Entity removed = entities.remove(e.getId());
		if (removed != null) {
			invalidateStreams();
			return true;
		}
		return false;
	}

	public Entity getEntity(int id) {
		return entities.get(id);
	}

	public void getEntities(Collection<? super Entity> out) {
		int count = entities.size();
		for (int n = 0; n < count; n++) {
			out.add(entities.valueAt(n));
		}
	}

	public int getEntitiesCount() {
		return entities.size();
	}

	public EntityStream joinStream(EntityStreamDef esd) {
		EntityStream stream = streamMap.get(esd);
		if (stream == null) {
			stream = newEntityStream(esd);
			streamMap.put(esd, stream);
		}
		return stream;
	}

	public boolean removeStream(EntityStreamDef esd) {
		return streamMap.remove(esd) != null;
	}

}
