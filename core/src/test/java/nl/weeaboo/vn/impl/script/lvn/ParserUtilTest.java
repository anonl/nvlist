package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.impl.script.lvn.ParserUtil;

public class ParserUtilTest {

    @Test
    public void readLinesUtf8() throws IOException {
        List<String> withBom = readLines("/script/utf8-with-bom.lvn");
        List<String> withoutBom = readLines("/script/utf8-without-bom.lvn");

        // Byte-order-mark is automatically skipped
        // In addition, the with-bom variant uses \r\n, while without-bom uses \n
        Assert.assertEquals(withoutBom, withBom);
    }

    @Test
    public void collapseWhitespace() {
        assertCollapsed(" x y z ", "  x  y  z  ", false);
        assertCollapsed("x y z", "  x  y  z  ", true);

        // The collapsible whitespace characters are [ \t\f] and zero width space
        assertCollapsed(" ", " \t\f\u200b", false);
        assertCollapsed(" ", "\u200b\f\t ", false);

        // A single zero width space collapses into itself
        assertCollapsed("\u200b", "\u200b", false);
        assertCollapsed("x\u200by", "x\u200by", false);

        // Newlines aren't collapsible
        assertCollapsed("x\n \ny", "x\n  \ny", false);

        // Trim only removes whitespace from the start and end of the string, not between newlines
        assertCollapsed("x\n \ny", "x\n  \ny", true);
    }

    @Test
    public void isWhitespaceOrWord() {
        // Character sequences consisting of only whitespace are considered whitespace
        // Character sequences containing one or more letters/digits are considered words

        assertWordType("", true, false);
        assertWordType(" \f\t\r\n", true, false);
        assertWordType("x", false, true);
        assertWordType("1", false, true);

        // Symbols are neither whitespace nor words
        assertWordType("()&-,.'\"", false, false);
    }

    private static void assertWordType(String str, boolean isWhitespace, boolean isWord) {
        Assert.assertEquals(isWhitespace, ParserUtil.isWhitespace(str));
        Assert.assertEquals(isWord, ParserUtil.isWord(str));
    }

    private void assertCollapsed(String expected, String input, boolean trim) {
        String actual = ParserUtil.collapseWhitespace(input, trim);
        Assert.assertEquals(expected, actual);
    }

    private List<String> readLines(String filename) throws IOException {
        InputStream in = ParserUtilTest.class.getResourceAsStream(filename);
        try {
            return ParserUtil.readLinesUtf8(in);
        } finally {
            in.close();
        }
    }

}
