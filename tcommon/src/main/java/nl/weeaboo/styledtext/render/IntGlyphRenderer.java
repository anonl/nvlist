package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public class IntGlyphRenderer extends DefaultGlyphRenderer {

	@Override
	public void drawGlyph(IGlyphRenderBuffer<?> grb, IGlyph glyph,
			double x, double y, double w, double h,
			double charsVisible, double charIndex)
	{
		if (charIndex <= charsVisible) {
			super.drawGlyph(grb, glyph, x, y, w, h, charsVisible, charIndex);
		}
	}

	//Functions
	
	//Getters
	
	//Setters
	
}
