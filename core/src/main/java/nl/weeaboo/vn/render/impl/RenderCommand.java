package nl.weeaboo.vn.render.impl;

public class RenderCommand implements Comparable<RenderCommand> {
	
	public final byte id;
	
	protected final int sortKey;
	
	protected RenderCommand(byte id, int key) {
		this.id = id;
		this.sortKey = key;
	}
	
	//Functions
	
	@Override
	public final int compareTo(RenderCommand c) {
		return (sortKey > c.sortKey ? 1 : (sortKey < c.sortKey ? -1 : 0));
	}
	
	//Getters
	
	//Setters
	
}
