package nl.weeaboo.styledtext.layout;

import static nl.weeaboo.styledtext.layout.LayoutUtil.reserveArray;
import nl.weeaboo.styledtext.TextStyle;

public class Spacing implements LineElement {

	private final float width, height;
	private final int bidiLevel;
	private final boolean isLineBreak;
	private final TextStyle style;
	
	public Spacing(float w, float h, TextStyle ts, boolean lb, int bidiLvl) {
		width = w;
		height = h;
		isLineBreak = lb;
		bidiLevel = bidiLvl;
		style = ts;
	}
	
	//Functions	
	@Override
	public LineElement[] split(LineElement[] out, float firstPartWidth) {
		if (firstPartWidth <= 0 || firstPartWidth >= width) {
			out = reserveArray(out, 1);
			out[0] = this;
		} else {
			out = reserveArray(out, 2);
			out[0] = new Spacing(firstPartWidth, height, style, false, bidiLevel);
			out[1] = new Spacing(width - firstPartWidth, height, style, isLineBreak, bidiLevel);
		}
		return out;
	}
		
	@Override
	public String toString() {
		return "Spacing{" + width + "}";
	}
	
	//Getters
	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public boolean isLineBreaking() {
		return isLineBreak;
	}

	@Override
	public int getBidiLevel() {
		return bidiLevel;
	}
	
	@Override
	public String getText() {
		return "";
	}
	
	@Override
	public TextStyle getStyle() {
		return style;
	}
	
	@Override
	public boolean isSpace() {
		return true;
	}
	
	@Override
	public boolean isWord() {
		return false;
	}
	
	//Setters
	
}
