package nl.weeaboo.lua2.io;

import java.io.Serializable;

public class RefEnvironment implements Serializable {
	
	private static final long serialVersionUID = -7946019173190923439L;
	
	public final long id;
	
	public RefEnvironment(long id) {
		this.id = id;
	}
	
}
