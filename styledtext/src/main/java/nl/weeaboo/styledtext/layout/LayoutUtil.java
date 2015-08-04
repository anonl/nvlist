package nl.weeaboo.styledtext.layout;

import java.text.Bidi;
import java.util.Arrays;
import java.util.List;

import nl.weeaboo.styledtext.ETextAttribute;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public final class LayoutUtil {

    private static final float EPSILON = .001f;

	private static final char NON_BREAKING_SPACE = 0x00A0;
	private static final char FIGURE_SPACE = 0x2007;
	private static final char NARROW_NO_BREAK_SPACE = 0x202F;
	private static final char WORD_JOINER = 0x2060;
	private static final char ZERO_WIDTH_NO_BREAK_SPACE = 0xFEFF;

	private LayoutUtil() {
	}

    /**
     * @param c A Unicode codepoint
     * @return {@code true} if the codepoint represents non-breaking whitespace
     */
	public static boolean isNonBreakingSpace(int c) {
		return c == NON_BREAKING_SPACE
		    || c == FIGURE_SPACE
		    || c == NARROW_NO_BREAK_SPACE
			|| c == WORD_JOINER
			|| c == ZERO_WIDTH_NO_BREAK_SPACE;
	}

	public static boolean hasMultipleStyles(StyledText stext) {
        if (stext.length() <= 1) {
            return false;
        }

        TextStyle ref = stext.getStyle(0);
        for (int n = 1; n < stext.length(); n++) {
            TextStyle ts = stext.getStyle(n);
            if (ref != ts && (ref == null || !ref.equals(ts))) {
                return true;
            }
        }
		return false;
	}

    public static ITextLayout layout(IFontStore fontStore, StyledText stext, LayoutParameters params) {
        TextLayoutAlgorithm algo = new TextLayoutAlgorithm(fontStore);
        return algo.layout(stext, params);
    }

    /**
     * @param pos Number of currently visible glyphs. The fractional part of the number represents partial
     *        visibility of the next glyph.
     * @param inc Increase the visible glyphs by this amount.
     * @return The new number of visible glyphs.
     */
    public static float increaseVisibleCharacters(IGlyphSequence glyphs, float pos, float inc) {
		pos = Math.max(0, pos);

        while (pos < glyphs.getGlyphCount() && inc > EPSILON) {
            int ipos = (int)pos;
            float fracLeft = (ipos + 1) - pos;
            float glyphDuration = getDuration(glyphs.getGlyphStyle(ipos));

            if (fracLeft >= 1f && inc >= glyphDuration) {
                // Fast case: move ahead a full glyph from an integer pos
                pos = ipos + 1;
                inc -= glyphDuration;
			} else {
                // Move ahead a partial glyph
                pos += Math.min(fracLeft, inc / glyphDuration);
                inc -= fracLeft * glyphDuration;
			}
		}

		return pos;
	}

    private static float getDuration(TextStyle style) {
        if (style == null || !style.hasProperty(ETextAttribute.speed)) {
            return 1f;
        }
        return 1f / style.getSpeed();
    }

    static float getKerningOffset(ILayoutElement a, ILayoutElement b) {
        if (!(a instanceof TextElement) || !(b instanceof IGlyphSequence)) {
            return 0f;
        }

        TextElement textA = (TextElement)a;
        IGlyphSequence textB = (IGlyphSequence)b;
        if (textB.getGlyphCount() == 0) {
            return 0f;
        }

        return textA.getKerning(textB.getGlyphId(0));
    }

    static int getGlyphCount(ILayoutElement elem) {
        if (elem instanceof IGlyphSequence) {
            IGlyphSequence seq = (IGlyphSequence)elem;
            return seq.getGlyphCount();
        }
        return 0;
    }

    static List<ILayoutElement> visualSortedCopy(List<ILayoutElement> elems) {
        ILayoutElement[] elemsArray = new ILayoutElement[elems.size()];
        byte[] bidiLevels = new byte[elemsArray.length];

        int t = 0;
        for (ILayoutElement elem : elems) {
            elemsArray[t] = elem;
            bidiLevels[t] = 0;
            if (elem instanceof TextElement) {
                TextElement textElem = (TextElement)elem;
                bidiLevels[t] = (byte)textElem.getBidiLevel();
            }
            t++;
        }

        Bidi.reorderVisually(bidiLevels, 0, elemsArray, 0, elemsArray.length);

        return Arrays.asList(elemsArray);
    }

    public static boolean isRightToLeftLevel(int bidiLevel) {
        return (bidiLevel & 1) != 0;
    }

}
