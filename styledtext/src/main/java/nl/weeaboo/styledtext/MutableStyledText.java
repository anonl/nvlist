package nl.weeaboo.styledtext;

import java.util.Arrays;

public class MutableStyledText extends AbstractStyledText<MutableStyledText> {

    private static final long serialVersionUID = 1L;

    public MutableStyledText() {
        this("");
    }
    public MutableStyledText(String text) {
        this(text, null);
    }
    public MutableStyledText(String text, TextStyle style) {
        super(text, style);
    }

    /** For internal use only */
    MutableStyledText(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        super(len, text, toff, styles, soff);
    }

    @Override
    MutableStyledText newInstance(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        return new MutableStyledText(len, text, toff, styles, soff);
    }

    public MutableStyledText copy() {
        // Copy arrays here because the constructor we call doesn't copy
        return new MutableStyledText(len, getChars(), 0, getStyles(), 0);
    }
    public StyledText immutableCopy() {
        // Copy arrays here because the constructor we call doesn't copy
        return new StyledText(len, getChars(), 0, getStyles(), 0);
    }

    public void append(AbstractStyledText<?> append) {
        int newLen = length() + append.length();
        ensureCapacity(newLen);

        append.getChars(text, toff + len, append.length());
        append.getStyles(styles, soff + len, append.length());
        len = newLen;
    }

    public void append(char c, TextStyle style) {
        int newLen = len + 1;
        ensureCapacity(newLen);

        text[toff + len] = c;
        styles[soff + len] = style;
        len = newLen;
    }

    private void ensureCapacity(int targetCapacity) {
        int capacity = capacity();
        if (capacity >= targetCapacity) {
            return;
        }

        targetCapacity = Math.max(capacity * 3 / 2, targetCapacity);

        char[] newText = new char[targetCapacity];
        TextStyle[] newStyles = new TextStyle[targetCapacity];

        int t = length();
        getChars(newText, 0, t);
        getStyles(newStyles, 0, t);

        text = newText;
        toff = 0;
        styles = newStyles;
        soff = 0;
    }

    private int capacity() {
        return text.length - toff;
    }

    public void setStyle(TextStyle style) {
        setStyle(style, 0, text.length);
    }

    public void setStyle(TextStyle style, int index) {
        setStyle(style, index, index + 1);
    }

    public void setStyle(TextStyle style, int from, int to) {
        if (from + 1 == to) {
            styles[from] = style;
        } else {
            Arrays.fill(styles, from, to, style);
        }
    }

    public void extendStyle(TextStyle ext) {
        extendStyle(TextStyle.replicate(ext, len));
    }
    public void extendStyle(int from, int to, TextStyle ext) {
        extendStyle(from, to, TextStyle.replicate(ext, to - from), 0);
    }
    public void extendStyle(TextStyle[] ext) {
        extendStyle(0, len, ext);
    }
    public void extendStyle(int from, int to, TextStyle[] ext) {
        extendStyle(from, to, ext, 0);
    }
    public void extendStyle(int from, int to, TextStyle[] ext, int eoff) {
        TextStyle.extend(styles, from, styles, from, ext, eoff, to - from);
    }

    public void setBaseStyle(TextStyle base) {
        setBaseStyle(TextStyle.replicate(base, len));
    }
    public void setBaseStyle(int from, int to, TextStyle base) {
        setBaseStyle(from, to, TextStyle.replicate(base, to - from), 0);
    }
    public void setBaseStyle(TextStyle[] base) {
        setBaseStyle(0, len, base);
    }
    public void setBaseStyle(int from, int to, TextStyle[] base) {
        setBaseStyle(from, to, base, 0);
    }
    public void setBaseStyle(int from, int to, TextStyle[] base, int boff) {
        TextStyle.extend(styles, from, base, boff, styles, from, to - from);
    }

}
