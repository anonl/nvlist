package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.text.Bidi;
import java.util.Arrays;

/**
 * @param <S> Self type. Used to automatically provide the correct subclass for methods that return a copy of
 *        this object.
 */
abstract class AbstractStyledText<S extends AbstractStyledText<S>> implements CharSequence, Serializable {

    private static final long serialVersionUID = 1L;

    protected int len;

    protected char[] text;
    protected int toff;

    protected TextStyle[] styles;
    protected int soff;

    protected AbstractStyledText(String str, TextStyle style) {
        len = str.length();
        text = str.toCharArray();

        styles = new TextStyle[len];
        Arrays.fill(styles, style);
    }

    /**
     * Note: Stores a reference to the supplied array args.
     */
    AbstractStyledText(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        this.len = len;
        this.text = text;
        this.toff = toff;
        this.styles = styles;
        this.soff = soff;
    }

    /**
     * Creates a new styled text object with the same type as this object.
     */
    abstract S newInstance(int len, char[] text, int toff, TextStyle[] styles, int soff);

    @Override
    public int hashCode() {
        return length();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractStyledText)) {
            return false;
        }

        AbstractStyledText<?> stext = (AbstractStyledText<?>)other;
        int len = length();
        if (len != stext.length()) {
            return false;
        }

        for (int n = 0; n < len; n++) {
            if (getChar(n) != stext.getChar(n)) {
                return false;
            }

            TextStyle s0 = getStyle(n);
            TextStyle s1 = stext.getStyle(n);
            if (s0 != s1 && (s0 == null || !s0.equals(s1))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The string represented by this styled text object, stripped of its styling information.
     */
    @Override
    public String toString() {
        return new String(text, toff, len);
    }

    protected final void checkBounds(int index) {
        if (index < 0 || index >= length()) {
            throw new ArrayIndexOutOfBoundsException("index=" + index + ", length=" + length());
        }
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public char charAt(int index) {
        checkBounds(index);
        return text[toff + index];
    }

    /**
     * @see #charAt(int)
     */
    public char getChar(int index) {
        checkBounds(index);
        return text[toff + index];
    }

    /**
     * @return The {@link TextStyle} at the specified index, or {@code null} if no style exists at the
     *         specified index.
     */
    public TextStyle getStyle(int index) {
        checkBounds(index);
        return styles[soff + index];
    }

    /**
     * @see #getChars(char[], int, int)
     */
    protected char[] getChars() {
        char[] out = new char[len];
        getChars(out, 0, len);
        return out;
    }

    /**
     * Copies characters from this styled text to the given output array.
     *
     * @param out The output array.
     * @param off Offset into the output array.
     * @param len Desired number of characters to copy.
     *
     * @throws ArrayIndexOutOfBoundsException If the given offset/length are invalid.
     *
     * @see #getStyles(TextStyle[], int, int)
     */
    protected void getChars(char[] out, int off, int len) {
        System.arraycopy(text, toff, out, off, len);
    }

    /**
     * @see #getStyled(TextStyle[], int, int)
     */
    protected TextStyle[] getStyles() {
        TextStyle[] out = new TextStyle[len];
        getStyles(out, 0, len);
        return out;
    }

    /**
     * Copies styles from this styled text to the given output array.
     *
     * @param out The output array.
     * @param off Offset into the output array.
     * @param len Desired number of objects to copy.
     *
     * @throws ArrayIndexOutOfBoundsException If the given offset/length are invalid.
     *
     * @see #getChars(char[], int, int)
     */
    protected void getStyles(TextStyle[] out, int off, int len) {
        System.arraycopy(styles, soff, out, off, len);
    }

    public boolean isBidi() {
        return Bidi.requiresBidi(text, toff, len);
    }

    /**
     * @param isRightToLeft If {@code true}, uses right-to-left as the default direction.
     */
    public Bidi getBidi(boolean isRightToLeft) {
        int flags = Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT;
        if (isRightToLeft) {
            flags = Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT;
        }

        return new Bidi(text, toff, null, 0, len, flags);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * @see #substring(int, int)
     * @see String#substring(int)
     */
    public S substring(int from) {
        return substring(from, length());
    }

    /**
     * @see String#substring(int, int)
     */
    public S substring(int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException(
                "Can't have a substring of negative size, from=" + from + " to=" + to);
        }

        // Check if to and from lie within the acceptable range
        checkBounds(from);
        if (to > from) {
            checkBounds(to - 1);
        }

        return newInstance(to - from, text, toff + from, styles, soff + from);
    }

    /**
     * @return A new styled text object; this styled text concatenated with an unstyled string.
     *
     * @see #concat(StyledText...)
     */
    public S concat(String text) {
        return concat(new StyledText(text));
    }

    /**
     * Creates a new styled text object by concatenating this styled text with the given array of other styled
     * text objects.
     */
    public S concat(AbstractStyledText<?>... stexts) {
        int newLen = length();
        for (AbstractStyledText<?> st : stexts) {
            newLen += st.length();
        }

        char[] newText = new char[newLen];
        TextStyle[] newStyles = new TextStyle[newLen];

        int t = length();
        getChars(newText, 0, t);
        getStyles(newStyles, 0, t);
        for (AbstractStyledText<?> st : stexts) {
            int stLen = st.length();
            st.getChars(newText, t, stLen);
            st.getStyles(newStyles, t, stLen);
            t += stLen;
        }

        return newInstance(newLen, newText, 0, newStyles, 0);
    }

}
