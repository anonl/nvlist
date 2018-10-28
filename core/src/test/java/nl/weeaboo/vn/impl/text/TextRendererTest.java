package nl.weeaboo.vn.impl.text;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.impl.core.StaticEnvironment;

public class TextRendererTest {

    private final ExceptionTester exTester = new ExceptionTester();

    private TextRenderer textRenderer;

    @Before
    public void before() {
        TestFontStore fontStore = new TestFontStore();
        textRenderer = new TextRenderer(fontStore);
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
        assertFinalLineFullyVisible(false);
        textRenderer.increaseVisibleText(999);
        assertVisibleText(0, 11); // Capped to number of glyphs in text
        assertFinalLineFullyVisible(true);
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

    /**
     * Calculate per-line visual bounds.
     */
    @Test
    public void testLineBounds() {
        textRenderer.setText("one\ntwo\nthree");

        // Each glyph is 32x32
        assertLineBounds(0, Rect2D.of(0,  0, 3 * 32, 32));
        assertLineBounds(1, Rect2D.of(0, 32, 3 * 32, 32));
        assertLineBounds(2, Rect2D.of(0, 64, 5 * 32, 32));
        Assert.assertEquals(64.0, textRenderer.getTextHeight(1, 3), 0.0);

        // Out-of-bounds line indices return an empty rect
        assertLineBounds(-1, Rect2D.EMPTY);
        Assert.assertEquals(3, textRenderer.getEndLine());
        assertLineBounds(4, Rect2D.EMPTY);

        // Empty text has empty bounds
        textRenderer.setText("");
        Assert.assertEquals(0.0, textRenderer.getNativeWidth(), 0.0);
        Assert.assertEquals(0.0, textRenderer.getNativeHeight(), 0.0);
        assertLineBounds(0, Rect2D.EMPTY);
    }

    private void assertLineBounds(int lineIndex, Rect2D expectedBounds) {
        Rect2D actualBounds = textRenderer.getLineBounds(lineIndex);
        RectAssert.assertEquals(expectedBounds, actualBounds, 0.0);
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

    private void assertFinalLineFullyVisible(boolean expected) {
        Assert.assertEquals(expected, textRenderer.isFinalLineFullyVisible());
    }

}
