package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.io.StreamUtil;

final class ParserUtil {

    private static final char ZERO_WIDTH_SPACE = 0x200B;

    private ParserUtil() {
    }

    public static List<String> readLinesUtf8(InputStream in) throws IOException {
        byte[] bytes = ByteStreams.toByteArray(in);
        int off = StreamUtil.skipBOM(bytes, 0, bytes.length);

        ImmutableList.Builder<String> lines = ImmutableList.builder();
        while (off < bytes.length) {
            int lineStart = off;
            while (off < bytes.length && bytes[off] != '\n') {
                off++;
            }

            int lineEnd = off;
            if (lineEnd > lineStart && bytes[lineEnd - 1] == '\r') { // CRLF
                lines.add(StringUtil.fromUTF8(bytes, lineStart, Math.max(0, lineEnd - lineStart - 1)));
            } else { // LF
                lines.add(StringUtil.fromUTF8(bytes, lineStart, lineEnd - lineStart));
            }

            off = lineEnd + 1;
        }
        return lines.build();
    }

    public static boolean isCollapsibleSpace(char c) {
        return c == ' ' || c == '\t' || c == '\f' || c == ZERO_WIDTH_SPACE;
    }

    public static String collapseWhitespace(String s, boolean trim) {
        char[] chars = new char[s.length()];
        s.getChars(0, chars.length, chars, 0);

        int r = 0;
        int w = 0;
        while (r < chars.length) {
            char c = chars[r++];

            if (isCollapsibleSpace(c)) {
                int collapseLength = 1;

                //Skip any future characters if they're whitespace
                while (r < chars.length && isCollapsibleSpace(chars[r])) {
                    r++;
                    collapseLength++;
                }

                if (w == 0 && trim) {
                    continue; //Starts with space
                } else if (r >= chars.length && trim) {
                    continue; //Ends with space
                }

                // Replace with ' ', unless the whitespace sequence consists of a single zero width space
                if (collapseLength > 1 || c != ZERO_WIDTH_SPACE) {
                    c = ' ';
                }
            }

            chars[w++] = c;
        }

        return new String(chars, 0, w);
    }

    /**
     * @return {@code true} if the given string consists of only whitespace.
     */
    public static boolean isWhitespace(String string) {
        return isWhitespace(string, 0, string.length());
    }

    public static boolean isWhitespace(String string, int from, int to) {
        int n = from;
        while (n < to) {
            int c = string.codePointAt(n);
            if (!Character.isWhitespace(c)) {
                return false;
            }
            n += Character.charCount(c);
        }
        return true;
    }

    public static boolean isWord(String string) {
        return isWord(string, 0, string.length());
    }

    public static boolean isWord(String string, int from, int to) {
        int n = from;
        while (n < to) {
            int c = string.codePointAt(n);
            if (Character.isLetterOrDigit(c)) {
                return true;
            }
            n += Character.charCount(c);
        }
        return false;
    }

    static int findBlockEnd(String str, int off, char endChar) {
        CharacterIterator itr = new StringCharacterIterator(str, off);
        return findBlockEnd(itr, endChar, null);
    }

    static int findBlockEnd(CharacterIterator itr, char endChar, StringBuilder out) {
        boolean inQuotes = false;
        int brackets = 0;

        for (char c = itr.current(); c != CharacterIterator.DONE; c = itr.next()) {
            if (c == '\\') {
                if (out != null) {
                    out.append(c);
                }
                c = itr.next();
            } else if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (brackets <= 0 && c == endChar) {
                    break;
                } else if (c == '[') {
                    brackets++;
                } else if (c == ']') {
                    brackets--;
                }
            }

            if (out != null && c != CharacterIterator.DONE) {
                out.append(c);
            }
        }
        return itr.getIndex();
    }

}
