package nl.weeaboo.entity;

import java.io.Serializable;

/**
 * Stores information about a part registered through a {@link PartRegistry}. 
 */
public final class PartType<T extends Part> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int id;
	private final String name;
	private final Class<T> partClass;
	
	public PartType(int id, String name, Class<T> partClass) {
		this.id = id;
		this.name = name;
		this.partClass = partClass;
	}
	
	public T cast(Part part) {
		return partClass.cast(part);
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
			&& partClass == pt.partClass;
	}
	
	public int getId() { return id; }
	public String getName() { return name; }
	public Class<T> getPartClass() { return partClass; }
	
}