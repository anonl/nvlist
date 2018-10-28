package nl.weeaboo.vn.impl.scene;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.layout.LayoutTester;
import nl.weeaboo.vn.impl.text.TestFontStore;
import nl.weeaboo.vn.impl.text.TextRenderer;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public final class TextDrawableLayoutTest {

    private static final double FONT_SIZE = 32.0; // Font size used by TestFontStore

    private final LayoutTester tester = new LayoutTester();

    private TextDrawable td;
    private ILayoutElem layoutElem;

    @Before
    public void before() {
        TestFontStore fontStore = new TestFontStore();

        td = new TextDrawable(new TextRenderer(fontStore));
        td.setText("a b c");

        layoutElem = td.getLayoutAdapter();
    }

    @Test
    public void testCalculateHeight() {
        tester.assertCalculatedHeight(layoutElem, 10, LayoutSizeType.MIN, LayoutSize.of(0));

        // a
        // b
        // c
        tester.assertCalculatedHeight(layoutElem, 10, LayoutSizeType.PREF, LayoutSize.of(3 * FONT_SIZE));
        tester.assertCalculatedHeight(layoutElem, 10, LayoutSizeType.MAX, LayoutSize.of(3 * FONT_SIZE));

        // a b
        // c
        tester.assertCalculatedHeight(layoutElem, 100, LayoutSizeType.PREF, LayoutSize.of(2 * FONT_SIZE));
    }
}
