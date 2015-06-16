package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public class DefaultGlyphRenderer implements IGlyphRenderer {

	//Functions
	@Override
	public void drawGlyph(IGlyphRenderBuffer<?> grb, IGlyph glyph,
			double x, double y, double w, double h,
			double charsVisible, double charIndex)
	{
		grb.bufferGlyph(glyph, x, y, w, h);
	}

	//Getters
	
	//Setters
	
}
