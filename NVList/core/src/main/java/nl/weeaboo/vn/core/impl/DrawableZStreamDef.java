package nl.weeaboo.vn.core.impl;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.EntityStreamDef;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.ILayer;

final class DrawableZStreamDef extends EntityStreamDef {

	private final ILayer layer;
	private final PartType<? extends IDrawablePart> drawablePart;
	private final int direction;

	public DrawableZStreamDef(ILayer layer, PartType<? extends IDrawablePart> part, int direction) {
		this.layer = layer;
		this.drawablePart = part;
		this.direction = direction;
	}

	@Override
	public int compare(Entity a, Entity b) {
		short az = a.getPart(drawablePart).getZ();
		short bz = b.getPart(drawablePart).getZ();
		return az < bz ? -direction : (az == bz ? 0 : direction);
	}

	@Override
	public boolean accept(Entity e) {
		IDrawablePart part = e.getPart(drawablePart);
		return part != null && part.getParentLayer() == layer;
	}

}