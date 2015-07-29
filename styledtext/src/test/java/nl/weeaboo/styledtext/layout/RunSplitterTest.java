package nl.weeaboo.styledtext.layout;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.styledtext.StyledText;

public class RunSplitterTest {

    @Test
    public void whitespaceSplit() {
        assertSplit("test test", "test", " ", "test");
        assertSplit(" test ", " ", "test", " ");
        assertSplit("  test  ", "  ", "test", "  ");
        assertSplit(" \t \n", " \t", " ", "\n");
    }

    @Test
    public void newlineSplit() {
        assertSplit("\ntest", "\n", "test");
        assertSplit("test\n", "test", "\n");
        assertSplit(" test\n", " ", "test", "\n");
        assertSplit(" test \n", " ", "test", " ", "\n");
        assertSplit("\n\n", "\n", "\n");
    }

    @Test
    public void nonBreakingSpace() {
        assertSplit("test\u00A0test", "test\u00A0test");
    }

    @Test
    public void softHyphen() {
        assertSplit("test\u00ADtest", "test\u00AD", "test");
    }

    @Test
    public void bidi() {
        // Splits on text direction changes
        assertSplit("abcעבריתabc", "abc", "עברית", "abc");
    }

    /** Checks where line break opportunities are detected within non-whitespace sequences */
    @Test
    public void wordBreaking() {
        assertSplit("can't", "can't");
        assertSplit("hyphenated-word", "hyphenated-", "word");
        assertSplit("double -- hyphens", "double", " ", "--", " ", "hyphens");
        assertSplit("test... test", "test...", " ", "test");
        assertSplit("test \"quoted\" test", "test", " ", "\"quoted\"", " ", "test");
    }

    private void assertSplit(String str, String... chunks) {
        TestRunHandler runHandler = new TestRunHandler();
        RunSplitter.run(new StyledText(str), false, runHandler);

        Assert.assertEquals(Arrays.asList(chunks), runHandler.getStrings());
    }

}
