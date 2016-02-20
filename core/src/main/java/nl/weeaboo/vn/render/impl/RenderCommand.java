package nl.weeaboo.vn.render.impl;

public class RenderCommand implements Comparable<RenderCommand> {
	
    static final byte ID_LAYER_RENDER_COMMAND  = 8;
    static final byte ID_QUAD_RENDER_COMMAND   = 9;
    static final byte ID_DISTORT_QUAD_COMMAND  = 12;
    static final byte ID_TEXT_RENDER_COMMAND   = 13;
    static final byte ID_CUSTOM_RENDER_COMMAND = 14;
    static final byte ID_SCREENSHOT_RENDER_COMMAND = 15;
    
	public final byte id;
	
	protected final int sortKey;
	
	protected RenderCommand(byte id, int key) {
		this.id = id;
		this.sortKey = key;
	}
	
	@Override
	public final int compareTo(RenderCommand c) {
		return (sortKey > c.sortKey ? 1 : (sortKey < c.sortKey ? -1 : 0));
	}
	
}
