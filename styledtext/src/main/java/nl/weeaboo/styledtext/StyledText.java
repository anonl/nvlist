package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

public final class StyledText extends AbstractStyledText<StyledText> {

    public static final StyledText EMPTY_STRING = new StyledText();

    private static final long serialVersionUID = 1L;

    private StyledText() {
        this("");
    }
    public StyledText(String text) {
        this(text, null);
    }
    public StyledText(String text, TextStyle style) {
        super(text, style);
    }

    /** For internal use only */
    StyledText(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        super(len, text, toff, styles, soff);
    }

    @Override
    StyledText newInstance(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        return new StyledText(len, text, toff, styles, soff);
    }

    public MutableStyledText mutableCopy() {
        // Copy arrays here because the constructor we call doesn't copy
        return new MutableStyledText(len, getChars(), 0, getStyles(), 0);
    }

    /**
     * @see #getCharacterIterator(int, int)
     */
    public CharacterIterator getCharacterIterator() {
        return getCharacterIterator(0, length());
    }

    public CharacterIterator getCharacterIterator(int from, int to) {
        if (from < 0 || to < from || to > length()) {
            throw new IllegalArgumentException("Invalid substring, from=" + from + " to=" + to);
        }

        return new CharArrayIterator(text, toff + from, to - from);
    }

}
