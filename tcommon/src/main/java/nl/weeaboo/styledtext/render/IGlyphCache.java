package nl.weeaboo.styledtext.render;

import nl.weeaboo.styledtext.layout.IGlyph;

public interface IGlyphCache {

	/**
	 * @return <code>true</code> if text rendering requires a separate shadow
	 *         layer, <code>false</code> is there's not shadow or the shadow is
	 *         baked into the glyph.
	 */
	public boolean usesSeparateShadow();

	/**
	 * @return <code>true</code> if different colored versions of this glyph can
	 *         simply change the current rendering color, <code>false</code> if
	 *         a separate glyph is required to draw in a different color.
	 */
	public boolean canColorizeForeground();
	
	/**
	 * Returns the glyph corresponding to the specified Unicode codepoint.
	 */
	public IGlyph getGlyph(int codepoint);
	
	/**
	 * Returns the glyph corresponding to the specified String. 
	 */
	public IGlyph getGlyph(String chars);
	
}
