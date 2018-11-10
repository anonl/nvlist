package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.text.MockTextRenderer;
import nl.weeaboo.vn.impl.text.TextLog;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.text.ITextRenderer;

public final class ScreenTextStateTest {

    private ScreenTextState textState;
    private ITextDrawable textDrawable;

    @Before
    public void before() {
        textDrawable = new TextDrawable(new MockTextRenderer());

        textState = new ScreenTextState(new TextLog());
    }

    @Test
    public void testSetText() {
        textState.setTextDrawable(textDrawable);

        // Set text (unstyled)
        textState.setText("unstyled");
        Assert.assertEquals(new StyledText("unstyled"), textDrawable.getText());

        textDrawable.setVisibleText(2);

        // Set text (styled)
        StyledText bold = new StyledText("bold", TextStyle.BOLD);
        textState.setText(bold);
        Assert.assertEquals(bold, textDrawable.getText());
        // Visible text is reset to 0.0 when setText() is called
        Assert.assertEquals(0, textDrawable.getVisibleText(), 0.0);
    }

    @Test
    public void testAppendText() {
        textState.setTextDrawable(textDrawable);

        textState.setText("base");
        textDrawable.setVisibleText(2);

        // Append text (unstyled)
        textState.appendText("unstyled");
        Assert.assertEquals(new StyledText("baseunstyled"), textDrawable.getText());
        // The amount of partially visible text is unchanged when appending text
        Assert.assertEquals(2, textDrawable.getVisibleText(), 0.0);

        // Append text (styled)
        StyledText bold = new StyledText("bold", TextStyle.BOLD);
        textState.appendText(bold);
        MutableStyledText mst = new MutableStyledText("baseunstyled");
        mst.append(bold);
        StyledText expected = mst.immutableCopy();
        Assert.assertEquals(expected, textDrawable.getText());

        /*
         * When the visible text is set to all-glyphs-visible (-1), appending new text resets the visible text
         * to the limit.
         */
        textState.setText("test");
        textDrawable.setVisibleText(ITextRenderer.ALL_GLYPHS_VISIBLE);
        textState.appendText("xy");
        Assert.assertEquals(4.0, textDrawable.getVisibleText(), 0.0);
    }

    @Test
    public void testTextSpeed() {
        textState.setTextDrawable(textDrawable);
        textState.setTextSpeed(123);

        Assert.assertEquals(123, textDrawable.getTextSpeed(), 0.0);
    }

    /**
     * The text state should be able to function without an attached text drawable.
     */
    @Test
    public void testNoTextDrawableSet() {
        textState.setText("ab");
        textState.appendText("cd");
        textState.setTextSpeed(123);
        textState.setTextSpeed(123);

        textState.setTextDrawable(textDrawable);
        textState.setTextDrawable(textDrawable);

        Assert.assertEquals(new StyledText("abcd"), textDrawable.getText());
        Assert.assertEquals(123, textDrawable.getTextSpeed(), 0.0);

    }

}
