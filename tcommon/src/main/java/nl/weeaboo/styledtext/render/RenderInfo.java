package nl.weeaboo.styledtext.render;

public final class RenderInfo extends AbstractRenderInfo {

	public RenderInfo(float cx, float cy, float bx, float by, float bw, float bh) {
		init(cx, cy, bx, by, bw, bh);
	}

	public MutableRenderInfo mutableCopy() {
		MutableRenderInfo mri = new MutableRenderInfo();
		mri.init(getCursorX(), getCursorY(), getX(), getY(), getWidth(), getHeight());
		return mri;
	}
	
}
