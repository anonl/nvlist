package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IGlyphStore {

	public IGlyph getGlyph(TextStyle style, int codepoint);
	public IGlyph getGlyph(TextStyle style, String chars);
	public float getLineHeight(TextStyle style);
	
}
