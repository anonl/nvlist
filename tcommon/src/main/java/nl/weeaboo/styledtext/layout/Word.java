package nl.weeaboo.styledtext.layout;

import static nl.weeaboo.styledtext.layout.LayoutUtil.reserveArray;
import nl.weeaboo.styledtext.TextStyle;

public class Word implements LineElement {

	private final CharSequence text;	
	private final TextStyle style;
	private final IGlyph[] glyphs;
	private final float width, height;
	private final int bidiLevel;
	
	public Word(CharSequence txt, TextStyle ts, IGlyph[] gs, float w, float h, int bidiLvl) {
		if (ts == null) throw new IllegalArgumentException("Style may not be null");
		
		text = txt;
		style = ts;
		glyphs = gs;
		width = w;
		height = h;
		bidiLevel = bidiLvl;		
	}
	
	//Functions	
	static IGlyph[] copyOfRange(IGlyph[] arr, int from, int to) {
		IGlyph[] result = new IGlyph[to-from];
		if (to > from) System.arraycopy(arr, from, result, 0, result.length);
		return result;
	}
		
	@Override
	public LineElement[] split(LineElement[] out, float splitX) {
		int gpos = 0;
		int tpos = 0;
		float curWidth = 0;		
		for (IGlyph glyph : glyphs) {
			float gw = glyph.getLayoutWidth();
			if (curWidth + gw > splitX) {
				if (gpos > 0) { //Force at least one glyph into the first part
					break;
				}
			}
			
			tpos += glyph.getCharCount();
			gpos += 1;
			curWidth += gw;
		}
		
		out = reserveArray(out, 2);
		int t = 0;		
		if (tpos > 0) {
			IGlyph[] gs = copyOfRange(glyphs, 0, gpos);
			out[t++] = new Word(text.subSequence(0, tpos), style, gs, curWidth, height, bidiLevel);
		}
		if (tpos < text.length()) {
			IGlyph[] gs = copyOfRange(glyphs, gpos, glyphs.length);
			out[t++] = new Word(text.subSequence(tpos, text.length()), style, gs, width-curWidth, height, bidiLevel);
		}
		if (out.length > t) {
			out[t++] = null; //Set trailing null value
		}
		return out;
	}
	
	/**
	 * Returns a (possibly new) word by removing all glyphs between positions (from, to) matching the given string.
	 */
	public Word withoutGlyphs(String glyphString, int from, int to) {
		boolean containsGlyphString = false;
		for (IGlyph g : glyphs) {
			if (glyphString.equals(g.getChars())) {
				containsGlyphString = true;
				break;
			}
		}
		
		//Good, we don't have to do anything.
		if (!containsGlyphString) {
			return this;
		}
		
		//Build a new word by copying non-matching glyphs one-by-one
		IGlyph[] dstG = new IGlyph[glyphs.length];
		StringBuilder dstT = new StringBuilder(text.length());
		
		int d = 0;
		int t = 0;
		float w = 0;
		float h = 0;
		for (int n = 0; n < glyphs.length; n++) {
			IGlyph g = glyphs[n];
			if (n < from || n >= to || !glyphString.equals(g.getChars())) {
				dstG[d++] = g;
				dstT.append(text, t, t+g.getCharCount());
				w += g.getLayoutWidth();
				h = Math.max(h, g.getLineHeight());
			}
			t += g.getCharCount();
		}
		
		return new Word(dstT.toString(), style, copyOfRange(dstG, 0, d), w, h, bidiLevel);
	}
		
	@Override
	public String toString() {
		return "Word{" + text + "}";
	}
	
	//Getters
	@Override
	public float getWidth() {
		return getWidth(0, getGlyphCount());
	}
	
	public float getWidth(int from, int to) {
		if (from <= 0 && to >= getGlyphCount()) {
			return width;
		}
		
		int n = 0;
		float w = 0;
		for (IGlyph g : glyphs) {
			if (n >= to) {
				break;
			}
			if (n >= from) {
				w += g.getLayoutWidth();
			}
			n++;
		}
		return w;
	}
	
	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public int getBidiLevel() {
		return bidiLevel;
	}
	
	@Override
	public boolean isLineBreaking() {
		return false;
	}

	@Override
	public String getText() {
		return text.toString();
	}

	public IGlyph getGlyph(int index) {
		return glyphs[index];
	}
	
	public int getGlyphCount() {
		return glyphs.length;
	}

	@Override
	public boolean isSpace() {
		return false;
	}
	
	@Override
	public boolean isWord() {
		return true;
	}
	
	@Override
	public TextStyle getStyle() {
		return style;
	}
		
	//Setters
	
}
