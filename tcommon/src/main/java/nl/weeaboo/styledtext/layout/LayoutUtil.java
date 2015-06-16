package nl.weeaboo.styledtext.layout;

import java.util.Iterator;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextAttribute;
import nl.weeaboo.styledtext.TextStyle;

public final class LayoutUtil {

	static final char SOFT_HYPHEN = 0x00AD;
			
	private static final char NON_BREAKING_SPACE = 0x00A0;
	private static final char FIGURE_SPACE = 0x2007;
	private static final char NARROW_NO_BREAK_SPACE = 0x202F;
	private static final char WORD_JOINER = 0x2060;
	private static final char ZERO_WIDTH_NO_BREAK_SPACE = 0xFEFF;	
	
	private LayoutUtil() {		
	}

	public static boolean isNonBreakingSpace(int c) {
		return c == NON_BREAKING_SPACE || c == FIGURE_SPACE || c == NARROW_NO_BREAK_SPACE
			|| c == WORD_JOINER || c == ZERO_WIDTH_NO_BREAK_SPACE;
	}
	
	/**
	 * Sets a trailing <code>null</code> value in <code>out</code> if larger
	 * than <code>size</code>.
	 * 
	 * @return <code>out</code> if <code>size</code> or larger, otherwise
	 *         allocates a new array of <code>size</code> elements.
	 */
	static LineElement[] reserveArray(LineElement[] out, int size) {
		if (out == null || out.length < size) {
			out = new LineElement[size];
		} else if (out.length > size) {
			out[size] = null;
		}
		return out;
	}
	
	public static boolean hasMultipleStyles(StyledText stext) {
		if (stext.length() <= 1) {
			return false;
		}
		
		TextStyle cur = stext.getStyle(0);
		for (int n = 1; n < stext.length(); n++) {
			TextStyle ts = stext.getStyle(n);
			if (cur != ts && (cur == null || !cur.equals(ts))) {
				return true;
			}
		}
		
		return false;
	}

	public static float increaseVisibleCharacters(TextLayout tl, float pos, float inc) {
		pos = Math.max(0, pos);
		
		int off = 0;
		Iterator<Word> itr = tl.wordIterator();
		Word word = null;
		
		outer:
		while (inc > 0.001) {
			while (word == null || pos > off + word.getGlyphCount()) {
				if (word != null) {
					off += word.getGlyphCount();
				}
				if (!itr.hasNext()) {
					break outer;
				}
				word = itr.next();
			}
			
			TextStyle style = word.getStyle();
				
			int itextPos = (int)pos;
			float fracLeft = (itextPos + 1) - pos;
			float charDuration = 1f;			
			if (style != null && style.hasProperty(TextAttribute.speed)) {
				//System.out.println(stext.charAt(itextPos) + " " + style);
				charDuration = 1f / style.getSpeed();
			}
	
			if (fracLeft >= 1f && inc >= charDuration) {
				pos = itextPos + 1;
				inc -= charDuration;
			} else {
				//System.out.println(textPos + " " + fracLeft + " | " +  (inc / charDuration) + " " + inc + " " + charDuration);
				pos += Math.min(fracLeft, inc / charDuration);
				inc -= fracLeft * charDuration;
			}
		}
		
		return pos;
	}
	
}
