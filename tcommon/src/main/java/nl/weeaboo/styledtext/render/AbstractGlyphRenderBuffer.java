package nl.weeaboo.styledtext.render;

public abstract class AbstractGlyphRenderBuffer<G> implements IGlyphRenderBuffer<G> {

	protected int color;	

	public AbstractGlyphRenderBuffer() {
		color = 0xFFFFFFFF;		
	}
	
	//Functions
	
	//Getters
	@Override
	public int getColor() {
		return color;
	}
	
	//Setters
	@Override
	public void mixColor(int argb) {
		color = TextRenderUtil.mixColor(color, argb);
	}
	
	@Override
	public void setColor(int argb) {
		color = argb;
	}
	
}
