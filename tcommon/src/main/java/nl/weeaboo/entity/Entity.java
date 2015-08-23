package nl.weeaboo.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import nl.weeaboo.io.IReadResolveSerializable;
import nl.weeaboo.io.IWriteReplaceSerializable;

/**
 * Game object. An entity is a group of related {@link Part} objects with a shared lifetime. For example an
 * entity may consist of a model part containing logical state and a renderer part that draws something to the
 * screen, based on the values in the model. This design allows easy mixing and matching of different parts as
 * opposed to more traditional inheritance-based OOP programming style.
 */
public final class Entity implements IWriteReplaceSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The minimum number of extra array elements that are added when the internal parts array needs to grow.
	 */
	private static final int MIN_PART_ARRAY_INC = 4;

	private static final Part[] EMPTY = {};


	// -------------------------------------------------------------------------
	// * Attributes must be serialized manually
	// * Update reset method after adding/removing attributes
	// -------------------------------------------------------------------------

	private int id; //Unique entity identifier

	/**
	 * The scene to which this entity is attached.
	 */
	transient Scene scene;

	/**
	 * <p>
	 * Array of parts attached to this entity. The array indices correspond to PartType ids. An entity may
	 * only store one part of each type.
	 *
	 * <p>
	 * The parts array only needs to be large enough to hold the highest PartType id currently attached to the
	 * entity. Ids that fall outside the array are treated as absent.
	 */
	private Part[] parts = EMPTY;

	/**
	 * The number of Part objects in the parts array.
	 */
	private int partsCount = 0;

	// -------------------------------------------------------------------------

	Entity(Scene scene, int id) {
		this.scene = scene;
		this.id = id;
	}

	@Override
	public Object writeReplace() throws ObjectStreamException {
		return new EntityRef(scene, id);
	}

	private void reset() {
		id = 0;
		scene = null;
		parts = EMPTY;
		partsCount = 0;
	}

	void serialize(ObjectOutput out) throws IOException {
		out.writeInt(id);

		final int partsL = parts.length;
		out.writeInt(partsCount);
		out.writeInt(partsL);
		for (int n = 0; n < partsL; n++) {
			if (parts[n] != null) {
				if (partsL < 256) {
					out.writeByte(n);
				} else {
					out.writeInt(n);
				}
				out.writeObject(parts[n]);
			}
		}
	}

	void deserialize(Scene s, ObjectInput in) throws IOException, ClassNotFoundException {
		reset();

		scene = s;
		id = in.readInt();

		partsCount = in.readInt();
		int partsL = in.readInt();
		parts = new Part[partsL];
		for (int n = 0; n < partsCount; n++) {
			int index = (partsL < 256 ? in.readByte() & 0xFF : in.readInt());
			parts[index] = (Part)in.readObject();
		}

		s.registerEntity(this, false);
	}

	/**
	 * Destroys this entity, detaching all parts from it and removing the entity from its scene.
	 */
	public final void destroy() {
		removeAllParts();
		if (scene != null) {
			scene.onEntityDestroyed(this);
		}
	}

	public final boolean isDestroyed() {
		return scene == null;
	}

	/**
	 * Moves this entity from its current Scene to <code>newScene</code>.
	 */
	public void moveToScene(Scene newScene) {
		if (scene == newScene) {
			return;
		}

		scene.unregisterEntity(this, true);
		scene = newScene;
		scene.registerEntity(this, true);
	}

	private void reserveRoomForPart(int partId) {
		Part[] oldParts = parts;
		Part[] newParts = new Part[Math.max(partId+1, oldParts.length + MIN_PART_ARRAY_INC)];
		if (partsCount > 0) { // No need to copy if the array was empty
			System.arraycopy(oldParts, 0, newParts, 0, oldParts.length);
		}
		parts = newParts;
	}

	public int getId() {
		return id;
	}

    public void handleSignal(ISignal signal) {
        for (Part part : parts) {
            if (signal.isHandled()) {
                break;
            }
            if (part != null) {
                part.handleSignal(signal);
            }
        }
    }

	/**
	 *  Returns an snapshot of all parts currently attached to this entity.
	 */
    Part[] parts() {
		Part[] result = new Part[partsCount];
		int r = 0;
		for (int index = 0; index < parts.length; index++) {
			Part p = parts[index];
			if (p != null) {
				result[r++] = p;
			}
		}
		return result;
	}

    public <T> void addPart(PartType<T> type, T part) {
        addPart(type.getId(), (Part)part); // Perform explicit cast for non-generic aware calling code
	}
	protected void addPart(int partId, Part part) {
		if (partId < parts.length && parts[partId] != null) {
			throw new IllegalArgumentException("Part index " + partId + " is already in use");
		}
		setPart(partId, part);
	}

	public void removeAllParts() {
		for (int n = 0; n < parts.length; n++) {
			if (parts[n] != null) {
				removePart(n);
			}
		}
	}

	public void removePart(PartType<?> type) {
		removePart(type.getId());
	}
	protected void removePart(int partId) {
		setPart(partId, null);
	}

	public boolean hasPart(PartType<?> type) {
		return hasPart(type.getId());
	}
	protected boolean hasPart(int partId) {
		return partId >= 0 && partId < parts.length && parts[partId] != null;
	}

    public <T> T getPart(PartType<T> type) {
		return type.cast(getPart(type.getId()));
	}
	protected Part getPart(int partId) {
		if (partId < 0 || partId >= parts.length) {
			return null;
		}
		return parts[partId];
	}

    public <T> void setPart(PartType<T> type, T part) {
        setPart(type.getId(), (Part)part); // Perform explicit cast for non-generic aware calling code
	}
	protected void setPart(int partId, Part part) {
		if (part != null) {
			//Set part
			if (partId >= parts.length) {
				reserveRoomForPart(partId);
			}

			Part oldPart = parts[partId];
			parts[partId] = part;

			if (oldPart == null) {
				partsCount++; // Filled an empty slot
			} else {
				if (scene != null) {
					scene.unregisterPart(this, oldPart, true);
				}
			}
			if (scene != null) {
				scene.registerPart(this, part, true);
			}
		} else if (partId >= 0 && partId < parts.length) {
			//Remove part
			Part oldPart = parts[partId];
			parts[partId] = null;

			if (oldPart != null) {
				partsCount--; // Cleared a filled slot
				if (scene != null) {
					scene.unregisterPart(this, oldPart, true);
				}
			}
		}
	}

	public int getPartsCount() {
		return partsCount;
	}

	protected Scene getScene() {
		return scene;
	}

	public String toDetailedString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entity{id=").append(id);
		for (Part p : parts()) {
			sb.append("\n  ").append(p.toDetailedString());
		}
		sb.append("\n}");
		return sb.toString();
	}

	private static class EntityRef implements IReadResolveSerializable {

		private static final long serialVersionUID = Entity.serialVersionUID;

		private final Scene scene;
		private final int id;

		public EntityRef(Scene s, int id) {
			this.scene = s;
			this.id = id;
		}

		@Override
		public Object readResolve() throws ObjectStreamException {
			return scene.getEntity(id);
		}

	}

}
