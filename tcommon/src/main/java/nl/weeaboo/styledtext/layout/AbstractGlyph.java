package nl.weeaboo.styledtext.layout;

import nl.weeaboo.common.Area2D;

public abstract class AbstractGlyph implements IGlyph {
	
	protected final String chars;	
	protected final Area2D visualBounds;
	protected final float layoutWidth;
	protected final float lineHeight;
	protected final boolean isMirrored;
	
	public AbstractGlyph(String chars, Area2D visualBounds, float layoutWidth, float lineHeight) {
		this.chars = chars;
		this.visualBounds = visualBounds;
		this.layoutWidth = layoutWidth;
		this.lineHeight = lineHeight;
		this.isMirrored = (chars.length() > 0 && Character.isMirrored(chars.codePointAt(0)));
	}
	
	//Functions
	
	//Getters
	@Override
	public String getChars() {
		return chars;
	}
	
	@Override
	public int getCharCount() {
		return chars.length();
	}
	
	@Override
	public boolean isMirrorGlyph() {
		return isMirrored;
	}

	@Override
	public float getLayoutWidth() {
		return layoutWidth;
	}

	@Override
	public float getLineHeight() {
		return lineHeight;
	}

	@Override
	public Area2D getVisualBounds() {
		return visualBounds;
	}
	
	//Setters

}
