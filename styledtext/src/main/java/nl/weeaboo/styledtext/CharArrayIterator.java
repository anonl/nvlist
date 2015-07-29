package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

/**
 * Character iterator implementation that uses a {@code char[]}.
 */
final class CharArrayIterator implements CharacterIterator {

    private final char[] chars;
    private final int off;
    private final int len;

    private int pos;

    public CharArrayIterator(char[] chars, int off, int len) {
        this.chars = chars;
        this.off = off;
        this.len = len;
    }

    @Override
    public Object clone() {
        CharArrayIterator c = new CharArrayIterator(chars, off, len);
        c.pos = pos;
        return c;
    }

    @Override
    public char current() {
        if (pos >= 0 && pos < len) {
            return chars[off + pos];
        }
        return DONE;
    }

    @Override
    public char first() {
        pos = 0;
        return current();
    }

    @Override
    public char last() {
        pos = (len > 0 ? len - 1 : 0);
        return current();
    }

    @Override
    public char previous() {
        if (pos > 0) {
            pos--;
            return chars[off + pos];
        }
        pos = 0;
        return DONE;
    }

    @Override
    public char next() {
        if (pos < len - 1) {
            pos++;
            return chars[off + pos];
        }
        pos = len;
        return DONE;
    }

    @Override
    public char setIndex(int position) {
        if (position < 0 || position > len) {
            throw new IllegalArgumentException("Invalid index");
        }
        pos = position;
        return current();
    }

    @Override
    public int getBeginIndex() {
        return 0;
    }

    @Override
    public int getEndIndex() {
        return len;
    }

    @Override
    public int getIndex() {
        return pos;
    }

}