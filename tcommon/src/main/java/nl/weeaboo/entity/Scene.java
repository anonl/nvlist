package nl.weeaboo.entity;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.io.IReadResolveSerializable;
import nl.weeaboo.io.IWriteReplaceSerializable;

/**
 * The environment for the entity system. Entities get a reference to this class
 * when constructed.
 */
public final class Scene implements IWriteReplaceSerializable {

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// * Attributes must be serialized manually
	// * Update reset method after adding/removing attributes
	// -------------------------------------------------------------------------
	transient World world;

    private final int id; // Unique scene identifier
	private boolean enabled = true;

    private transient final EntityManager entityManager = new EntityManager(this);
    private transient final PartManager partManager = new PartManager(this);
    private transient final CopyOnWriteArrayList<IEntityListener> entityListeners = new CopyOnWriteArrayList<IEntityListener>();
    private transient final CopyOnWriteArrayList<IPartListener> partListeners = new CopyOnWriteArrayList<IPartListener>();

	// -------------------------------------------------------------------------

	Scene(World w, int id) {
		this.world = w;
		this.id = id;
	}

	@Override
	public Object writeReplace() throws ObjectStreamException {
		return new SceneRef(world, id);
	}

	private void reset() {
		enabled = true;

		entityManager.reset();
		partManager.reset();
		entityListeners.clear();
		partListeners.clear();
	}

	void serialize(ObjectOutput out) throws IOException {
		out.writeBoolean(enabled);

		entityManager.serialize(out);
		partManager.serialize();

		out.writeInt(entityListeners.size());
		for (IEntityListener listener : entityListeners) {
			out.writeObject(listener);
		}

		out.writeInt(partListeners.size());
		for (IPartListener listener : partListeners) {
			out.writeObject(listener);
		}
	}

    void deserialize(World w, ObjectInput in) throws IOException, ClassNotFoundException {
		reset();

        this.world = w;
		enabled = in.readBoolean();

		entityManager.deserialize(this, in);
		partManager.deserialize(this);

		int entityListenersL = in.readInt();
		for (int n = 0; n < entityListenersL; n++) {
			entityListeners.add((IEntityListener)in.readObject());
		}

		int partListenersL = in.readInt();
		for (int n = 0; n < partListenersL; n++) {
			partListeners.add((IPartListener)in.readObject());
		}
	}

	public final void destroy() {
		clear();

		if (world != null) {
			world.onSceneDestroyed(this);
		}
	}

	public final boolean isDestroyed() {
		return world == null;
	}

	public int getId() {
		return id;
	}

	/**
	 * @see #setEnabled(boolean)
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables this scene. The enabled state can be used by the
	 * application to temporarily mark a scene as paused.
	 */
	public void setEnabled(boolean e) {
		enabled = e;
	}

	public void addEntityListener(IEntityListener el) {
		entityListeners.add(el);
	}
	public void removeEntityListener(IEntityListener el) {
		entityListeners.remove(el);
	}

	public void addPartListener(IPartListener pl) {
		partListeners.add(pl);
	}
	public void removePartListener(IPartListener pl) {
		partListeners.remove(pl);
	}

	/**
	 * @see World#firePartPropertyChanged(Part, String, Object)
	 */
	void firePartPropertyChanged(Part part, String propertyName, Object newValue) {
		boolean invalidated = false;
		for (Entity e : partManager.entitiesWithPart(part)) {
			if (!invalidated) {
				invalidated = true;
				entityManager.invalidateStreams();
			}

			for (IPartListener pl : partListeners) {
				pl.onPartPropertyChanged(e, part, propertyName, newValue);
			}
		}
	}

	/**
	 * Creates a new entity and registers it with this world.
	 */
	public Entity createEntity() {
		Entity e = new Entity(this, entityManager.generateId());
		for (IEntityListener el : entityListeners) {
			el.onEntityCreated(e);
		}
		registerEntity(e, true);
		return e;
	}

	/**
	 * Removes a destroyed entity from this scene.
	 */
	void onEntityDestroyed(Entity e) {
		boolean destroyed = unregisterEntity(e, true);
		if (destroyed) {
			for (IEntityListener el : entityListeners) {
				el.onEntityDestroyed(e);
			}
		}
	}

	/**
	 * Registers an entity (used during deserialization).
	 */
	void registerEntity(Entity e, boolean notifyListeners) {
		e.scene = this;
		entityManager.add(e);

		if (notifyListeners) {
			for (IEntityListener el : entityListeners) {
				el.onEntityAttached(this, e);
			}
		}

		for (Part p : e.parts()) {
			registerPart(e, p, notifyListeners);
		}
	}

	/**
	 * Unregisters an entity (used when moving entities between Scenes).
	 *
	 * @return <code>true</code> if the entity was successfully removed,
	 *         <code>false</code> if the entity couldn't be removed because it
	 *         wasn't attached to this scene.
	 */
	boolean unregisterEntity(Entity e, boolean notifyListeners) {
		if (!entityManager.remove(e)) {
			return false;
		}
		e.scene = null;

		if (notifyListeners) {
			for (IEntityListener el : entityListeners) {
				el.onEntityDetached(this, e);
			}
		}

		for (Part p : e.parts()) {
			unregisterPart(e, p, notifyListeners);
		}
		return true;
	}

	/**
	 * Destroys and removes all entities from this scene.
	 */
	public void clear() {
		List<Entity> entities = getEntities();
		//Reverse order removes the entities more efficiently from the arraylist that stores them.
		Collections.reverse(entities);
		for (Entity e : entities) {
			e.destroy();
		}
	}

    /**
     * Creates (or joins an existing) EntityStream representing a filtered and sorted view of the collection
     * of entities.
     */
	public EntityStream joinStream(EntityStreamDef esd) {
		return entityManager.joinStream(esd);
	}

	/**
	 * @see #joinStream(EntityStreamDef)
	 */
	public boolean removeStream(EntityStreamDef esd) {
		return entityManager.removeStream(esd);
	}

	public List<Entity> getEntities() {
		List<Entity> out = new ArrayList<Entity>(getEntitiesCount());
		getEntities(out);
		return out;
	}
	public void getEntities(Collection<Entity> out) {
		entityManager.getEntities(out);
	}
	public int getEntitiesCount() {
		return entityManager.getEntitiesCount();
	}
	public Entity getEntity(int id) {
		return entityManager.getEntity(id);
	}
	public boolean contains(Entity e) {
		return e != null && contains(e.getId());
	}
	public boolean contains(int id) {
		return getEntity(id) != null;
	}

	void registerPart(Entity e, Part p, boolean notifyListeners) {
		p.refcount++;
		assert p.refcount >= 1;

		partManager.add(e, p);
		if (p.refcount == 1) {
			p.world = world;
			p.onAttached(world);
		}
		entityManager.invalidateStreams();

		if (notifyListeners) {
			for (IPartListener pl : partListeners) {
				pl.onPartAttached(e, p);
			}
		}
	}

	void unregisterPart(Entity e, Part p, boolean notifyListeners) {
		p.refcount--;
		assert p.refcount >= 0;

		partManager.remove(e, p);
		entityManager.invalidateStreams();

		if (notifyListeners) {
			for (IPartListener pl : partListeners) {
				pl.onPartDetached(e, p);
			}
		}

		if (p.refcount == 0) {
			p.world = null;
			p.onDetached(world);
		}
	}

	private static class SceneRef implements IReadResolveSerializable {

		private static final long serialVersionUID = Scene.serialVersionUID;

		private final World world;
		private final int id;

		public SceneRef(World w, int id) {
			this.world = w;
			this.id = id;
		}

		@Override
        public Object readResolve() throws ObjectStreamException {
            Scene scene = world.getScene(id);
            if (scene == null) {
                throw new InvalidObjectException("Scene lost during serialization: " + id);
            }
            return scene;
		}

	}

}
