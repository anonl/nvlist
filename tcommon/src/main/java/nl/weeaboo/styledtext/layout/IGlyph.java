package nl.weeaboo.styledtext.layout;

import nl.weeaboo.common.Area2D;

public interface IGlyph {

	public String getChars();
	public int getCharCount();
	public Area2D getVisualBounds();
	public boolean isMirrorGlyph();
	public float getLayoutWidth();
	public float getLineHeight();
	
}
