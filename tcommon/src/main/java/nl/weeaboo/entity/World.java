package nl.weeaboo.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.weeaboo.collections.IntMap;

/**
 * This class maintains a collection of all available {@link Scene} objects.
 */
public final class World implements Externalizable {

	private static final int SERIALIZE_VERSION = 6;

	// -------------------------------------------------------------------------
	// * Attributes must be serialized manually
	// * Update reset method after adding/removing attributes
	// -------------------------------------------------------------------------

	private PartRegistry partRegistry;
	private final IntMap<Scene> scenes = new IntMap<Scene>();
	private int idGenerator;

	// -------------------------------------------------------------------------

	public World() {
		this(new PartRegistry());
	}
	public World(PartRegistry partRegistry) {
		if (partRegistry == null) throw new IllegalArgumentException("partRegistry may not be null");

		this.partRegistry = partRegistry;
	}

	private void reset() {
        if (partRegistry != null) {
            partRegistry.clear();
        }
		scenes.clear();
		idGenerator = 0;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(SERIALIZE_VERSION);
		out.writeObject(partRegistry);

		final int scenesL = scenes.size();
		out.writeInt(scenesL);
		for (int n = 0; n < scenesL; n++) {
			Scene scene = scenes.valueAt(n);
            out.writeInt(scene.getId());
			scene.serialize(out);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version != SERIALIZE_VERSION) {
			throw new IOException("Unsupported serialization version: " + version);
		}

		reset();

		partRegistry = (PartRegistry)in.readObject();

		scenes.clear();
		int scenesL = in.readInt();
		for (int n = 0; n < scenesL; n++) {
			// Validate scene id
            int sceneId = in.readInt();
			if (sceneId <= 0) {
				throw new IOException("Serialized scene has an invalid id: " + sceneId);
			} else if (scenes.get(sceneId) != null) {
				throw new IOException("Serialized data contains the same ID multiple times: " + sceneId);
			}

            Scene scene = new Scene(this, sceneId);
			scenes.put(sceneId, scene);
            scene.deserialize(this, in);
		}
	}

	private int generateSceneId() {
		while (scenes.containsKey(++idGenerator)) {}
		return idGenerator;
	}

	/**
	 * Creates a new scene and attaches it to this world.
	 */
	public Scene createScene() {
		final int id = generateSceneId();
		assert !scenes.containsKey(id);

		Scene s = new Scene(this, id);
		scenes.put(id, s);
		return s;
	}

	/**
	 * Removes a destroyed scene from this world.
	 */
	void onSceneDestroyed(Scene s) {
		Scene removed = scenes.remove(s.getId());
		s.world = null;
		assert removed == s;
	}

	/**
	 * Returns the scene with the specified identifier.
	 */
	public Scene getScene(int id) {
		return scenes.get(id);
	}

	/**
	 * Returns the number of scenes inside this world.
	 */
	public int getScenesCount() {
		return scenes.size();
	}

	/**
	 * Returns a list of all scenes inside this world.
	 */
	public List<Scene> getScenes() {
		List<Scene> out = new ArrayList<Scene>(getScenesCount());
		getScenes(out);
		return out;
	}
	public void getScenes(Collection<Scene> out) {
		final int scenesCount = scenes.size();
		for (int n = 0; n < scenesCount; n++) {
			out.add(scenes.valueAt(n));
		}
	}

	/**
	 * Returns the global part registry used by this world.
	 */
	public PartRegistry getPartRegistry() {
		return partRegistry;
	}

	public Entity findEntity(int id) {
		int scenesL = scenes.size();
		for (int n = 0; n < scenesL; n++) {
			Entity e = scenes.valueAt(n).getEntity(id);
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets called by parts whenever one of their properties changes.
	 */
	void firePartPropertyChanged(Part part, String propertyName, Object newValue) {
		for (Scene scene : scenes.values()) {
			scene.firePartPropertyChanged(part, propertyName, newValue);
		}
	}
}
