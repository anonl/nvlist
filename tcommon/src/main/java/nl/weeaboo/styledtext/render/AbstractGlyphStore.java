package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IGlyph;
import nl.weeaboo.styledtext.layout.IGlyphStore;

public abstract class AbstractGlyphStore implements IGlyphStore {

	@Override
	public IGlyph getGlyph(TextStyle style, int codepoint) {
		return getGlyph(style, new String(Character.toChars(codepoint)));
	}

	@Override
	public float getLineHeight(TextStyle style) {
		return getGlyph(style, " ").getLineHeight();
	}

}
