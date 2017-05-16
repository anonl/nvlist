package nl.weeaboo.vn.impl.text;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.impl.core.StaticEnvironment;

public class TextRendererTest {

    private TextRenderer textRenderer;
    private ExceptionTester exTester;

    @Before
    public void before() {
        StaticEnvironment.FONT_STORE.set(new TestFontStore());

        exTester = new ExceptionTester();
        textRenderer = new TextRenderer();
    }

    @After
    public void after() {
        StaticEnvironment.FONT_STORE.set(null);
    }

    @Test
    public void testSetText() {
        // The default style doesn't actually influence the behavior of setText/getText
        textRenderer.setDefaultStyle(TextStyle.ITALIC);

        // setText(String) converts to an unstyled StyledText object internally
        textRenderer.setText("one");
        assertText(new StyledText("one"));

        // setText(StyledText) just sets the supplied text object
        textRenderer.setText(new StyledText("two", TextStyle.BOLD));
        assertText(new StyledText("two", TextStyle.BOLD));
    }

    @Test
    public void testOffsetY() {
        textRenderer.setText("test");
        textRenderer.setSize(200, 100);

        float textHeight = textRenderer.getTextHeight();
        Assert.assertEquals(32d, textHeight, 0f); // Default style has size 32

        assertOffsetY(0, VerticalAlign.TOP);
        assertOffsetY(50 - 16, VerticalAlign.MIDDLE);
        assertOffsetY(100 - 32, VerticalAlign.BOTTOM);

        // Check that all VerticalAlign values are covered
        for (VerticalAlign valign : VerticalAlign.values()) {
            TextRenderer.getOffsetY(textRenderer, valign);
        }

        // Null align is invalid
        exTester.expect(NullPointerException.class, () -> TextRenderer.getOffsetY(textRenderer, null));
    }

    @Test
    public void changeVisibleText() {
        // Initial state
        assertVisibleText(0, 0);

        // After changing the text, by default all glyphs become visible
        textRenderer.setText("abcdefghijklmnopqrstuvwxyz");
        assertVisibleText(0, 26); // Number of glyphs in the text

        // Set to a shorter text, the visible glyphs are reduced to the new text length
        textRenderer.setText("one\ntwo\nthree");
        assertVisibleText(0, 11);

        // Gradually increase visible glyphs, starting from 0
        textRenderer.setVisibleText(0);
        textRenderer.increaseVisibleText(1);
        assertVisibleText(0, 1);
        textRenderer.increaseVisibleText(3);
        assertVisibleText(0, 4);
        textRenderer.increaseVisibleText(999);
        assertVisibleText(0, 11); // Capped to number of glyphs in text
        // Negative increases are ignored
        textRenderer.increaseVisibleText(-1);
        assertVisibleText(0, 11);

        // Increment start line
        textRenderer.setVisibleText(1, 0);
        assertVisibleText(1, 0);
        textRenderer.setVisibleText(1, 999);
        assertVisibleText(1, 8); // Capped to number of glyphs from start line
    }

    /** Toggle right-to-left flag */
    @Test
    public void changeTextDirection() {
        Assert.assertFalse(textRenderer.isRightToLeft());
        textRenderer.setRightToLeft(true);
        Assert.assertTrue(textRenderer.isRightToLeft());
    }

    private void assertOffsetY(int expected, VerticalAlign valign) {
        Assert.assertEquals(expected, TextRenderer.getOffsetY(textRenderer, valign), 0f);
    }

    private void assertText(StyledText expected) {
        Assert.assertEquals(expected, textRenderer.getText());
    }

    private void assertVisibleText(int expectedStartLine, double expectedVisibleText) {
        Assert.assertEquals(expectedStartLine, textRenderer.getStartLine());
        Assert.assertEquals(expectedVisibleText, textRenderer.getVisibleText(), 0.01);
    }

}
