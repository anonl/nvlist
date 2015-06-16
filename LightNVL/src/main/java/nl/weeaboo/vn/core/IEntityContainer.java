package nl.weeaboo.vn.core;

import nl.weeaboo.entity.Entity;

/**
 * Interface for objects that can contain entities.
 */
public interface IEntityContainer {

	/**
	 * Attaches the specified entity to this layer and removes it from its old layer.
	 */
	public void add(Entity e);

	/**
	 * @return {@code true} if the specified entity is currently attached to this layer.
	 */
	public boolean contains(Entity e);

}
