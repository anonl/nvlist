package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.scene.TextDrawable;
import nl.weeaboo.vn.impl.text.MockTextRenderer;
import nl.weeaboo.vn.text.ITextRenderer;

public class TextDrawableTest {

    private final ExceptionTester extest = new ExceptionTester();

    private MockTextRenderer r;
    private TextDrawable td;

    @Before
    public void before() {
        r = new MockTextRenderer();
        td = new TextDrawable(r);
    }

    /**
     * Most methods in {@link TextDrawable} delegate to an embedded {@link ITextRenderer}.
     */
    @Test
    public void testDelegate() {
        // Visible text
        Assert.assertEquals(r.getStartLine(), td.getStartLine());
        Assert.assertEquals(r.getEndLine(), td.getEndLine());
        Assert.assertEquals(r.getLineCount(), td.getLineCount());
        Assert.assertEquals(r.getVisibleText(), td.getVisibleText(), 0.0);
        Assert.assertEquals(r.getMaxVisibleText(), td.getMaxVisibleText());
        Assert.assertEquals(r.isFinalLineFullyVisible(), td.isFinalLineFullyVisible());

        // Size
        Assert.assertEquals(r.getMaxWidth(), td.getMaxWidth(), 0.0);
        Assert.assertEquals(r.getMaxHeight(), td.getMaxHeight(), 0.0);
        Assert.assertEquals(r.getTextWidth(), td.getTextWidth(), 0.0);
        Assert.assertEquals(r.getTextHeight(), td.getTextHeight(), 0.0);
        Assert.assertEquals(r.getTextHeight(1, 2), td.getTextHeight(1, 2), 0.0);

        td.setMaxSize(11, 22);
        Assert.assertEquals(11, r.getMaxWidth(), 0.0);
        Assert.assertEquals(22, r.getMaxHeight(), 0.0);

        td.setUnscaledSize(33, 44);
        Assert.assertEquals(33, td.getUnscaledWidth(), 0.0);
        Assert.assertEquals(44, td.getUnscaledHeight(), 0.0);
    }

    @Test
    public void testTextSpeed() {
        extest.expect(IllegalArgumentException.class, () -> td.setTextSpeed(-1));
        extest.expect(IllegalArgumentException.class, () -> td.setTextSpeed(Double.NEGATIVE_INFINITY));
        extest.expect(IllegalArgumentException.class, () -> td.setTextSpeed(Double.POSITIVE_INFINITY));
        extest.expect(IllegalArgumentException.class, () -> td.setTextSpeed(Double.NaN));

        td.setTextSpeed(123);
        Assert.assertEquals(123, td.getTextSpeed(), 0.0);
    }

    @Test
    public void testRightToLeft() {
        td.setRightToLeft(false);
        Assert.assertEquals(false, td.isRightToLeft());
        td.setRightToLeft(true);
        Assert.assertEquals(true, td.isRightToLeft());
    }

    @Test
    public void testSetText() {
        td.setText("abc");
        Assert.assertEquals("abc", r.getText().toString());

        StyledText stext = new StyledText("def", TextStyle.BOLD);
        td.setText(stext);
        Assert.assertEquals(stext, td.getText());
        Assert.assertEquals(stext, r.getText());
    }

    @Test
    public void testSetDefaultStyle() {
        td.setDefaultStyle(TextStyle.BOLD);
        Assert.assertEquals(td.getDefaultStyle(), r.getDefaultStyle());

        td.extendDefaultStyle(TextStyle.ITALIC);
        Assert.assertEquals(TextStyle.BOLD_ITALIC, r.getDefaultStyle());
    }
}
