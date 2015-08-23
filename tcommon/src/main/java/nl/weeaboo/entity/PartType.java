package nl.weeaboo.entity;

import java.io.Serializable;

/**
 * Stores information about a part registered through a {@link PartRegistry}.
 */
public final class PartType<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int id;
	private final String name;
    private final Class<T> partInterface;

    public PartType(int id, String name, Class<T> partInterface) {
		this.id = id;
		this.name = name;
        this.partInterface = partInterface;
	}

	public T cast(Part part) {
        return partInterface.cast(part);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PartType)) {
			return false;
		}

		PartType<?> pt = (PartType<?>)obj;
		return id == pt.id
			&& name.equals(pt.name)
			&& partInterface == pt.partInterface;
	}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Class<T> getPartInterface() {
        return partInterface;
    }

}