package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public interface IGlyphRenderBuffer<G> {

	public void bufferGlyph(IGlyph g, double x, double y, double w, double h);
	public void flush(G g);
	
	public int getColor();
	
	public void mixColor(int argb);
	public void setColor(int argb);
	public void setColor(G g);
	
}
