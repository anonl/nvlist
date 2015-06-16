package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public interface IGlyphRenderer {

	public void drawGlyph(IGlyphRenderBuffer<?> grb, IGlyph glyph,
			double x, double y, double w, double h,
			double charsVisible, double charIndex);
	
}
