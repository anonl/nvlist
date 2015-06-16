package nl.weeaboo.entity;

import java.util.Comparator;

public abstract class EntityStreamDef implements Comparator<Entity> {

	@Override
	public abstract int compare(Entity a, Entity b);

	/**
	 * This method is used to determine which entities to include in the stream.
	 *
	 * @return <code>true</code> to include the entity, <code>false</code> to
	 *         exclude.
	 */
	public abstract boolean accept(Entity e);

}
