package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public class FloatGlyphRenderer extends DefaultGlyphRenderer {

	public FloatGlyphRenderer() {
		
	}
	
	//Functions
	@Override
	public void drawGlyph(IGlyphRenderBuffer<?> grb, IGlyph glyph,
			double x, double y, double w, double h,
			double charsVisible, double charIndex)
	{
		double alpha = (charsVisible - charIndex);
		if (alpha <= 0) {
			return;
		} else if (alpha >= 1) {
			super.drawGlyph(grb, glyph, x, y, w, h, charsVisible, charIndex);
		} else {
			int oldColor = grb.getColor();
			int ai = Math.max(0, Math.min(255, (int)(alpha * 255.0)));
			grb.setColor((ai<<24) | (oldColor & 0xFFFFFF));
			super.drawGlyph(grb, glyph, x, y, w, h, charsVisible, charIndex);
			grb.setColor(oldColor);
		}
	}
	
	//Getters
	
	//Setters
	
}
