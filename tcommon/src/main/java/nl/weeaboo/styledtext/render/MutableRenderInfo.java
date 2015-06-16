package nl.weeaboo.styledtext.render;

public final class MutableRenderInfo extends AbstractRenderInfo {

	public MutableRenderInfo() {		
	}
	
	public RenderInfo immutableCopy() {
		return new RenderInfo(getCursorX(), getCursorY(), getX(), getY(), getWidth(), getHeight());
	}
	
	public void set(float cx, float cy, float bx, float by, float bw, float bh) {
		init(cx, cy, bx, by, bw, bh);
	}
	
}
