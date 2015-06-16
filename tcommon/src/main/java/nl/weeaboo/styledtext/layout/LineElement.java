package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface LineElement {

	public LineElement[] split(LineElement[] out, float x);
	
	public float getWidth();
	public float getHeight();
	public int getBidiLevel();
	public boolean isLineBreaking();	
	public String getText();
	public TextStyle getStyle();
	public boolean isSpace();
	public boolean isWord();
	
}
