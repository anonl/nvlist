package nl.weeaboo.entity;

import java.util.Arrays;

public final class DefaultEntityStreamDef extends EntityStreamDef {

    public static final DefaultEntityStreamDef ALL_ENTITIES_STREAM = new DefaultEntityStreamDef();

	private final PartType<?>[] requiredParts;

	public DefaultEntityStreamDef(PartType<?>... required) {
		requiredParts = required.clone();
	}

	@Override
	public int hashCode() {
	    return Arrays.hashCode(requiredParts);
	}

	@Override
	public boolean equals(Object obj) {
	    if (!(obj instanceof DefaultEntityStreamDef)) {
	        return false;
	    }

	    DefaultEntityStreamDef def = (DefaultEntityStreamDef)obj;
	    return Arrays.equals(requiredParts, def.requiredParts);
	}

	@Override
	public int compare(Entity a, Entity b) {
		return 0;
	}

	@Override
	public boolean accept(Entity e) {
		for (PartType<?> partType : requiredParts) {
			if (!e.hasPart(partType)) {
				return false;
			}
		}
		return true;
	}

}
