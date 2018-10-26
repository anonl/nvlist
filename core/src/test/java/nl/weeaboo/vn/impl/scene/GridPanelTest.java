package nl.weeaboo.vn.impl.scene;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.scene.IVisualElement;

public final class GridPanelTest {

    private GridPanel panel;
    private IVisualElement a;
    private IVisualElement b;
    private IVisualElement c;
    private IVisualElement d;

    @Before
    public void before() {
        panel = new GridPanel();
        a = image();
        b = image();
        c = image();
        d = image();
    }

    /**
     * Tests the most commen behaviors.
     */
    @Test
    public void testLayout() {
        panel.add(a).grow();
        panel.add(b);
        panel.endRow();
        panel.add(c).growY();
        panel.add(d);

        panel.setBounds(0, 0, 100, 100);
        panel.validateLayout();

        assertLayout(a, 0, 0, 90, 50); // grow x/y
        assertLayout(b, 90, 0, 10, 10);
        assertLayout(c, 0, 50, 10, 50); // grow y
        assertLayout(d, 90, 50, 10, 10);

        // Removing a component can cause a change in the position of the other components
        panel.remove(a);
        panel.validateLayout();

        assertLayout(b, 0, 0, 10, 10);
        assertLayout(c, 0, 10, 10, 90); // grow y
        assertLayout(d, 50, 10, 10, 10);
    }

    /**
     * Resize the panel to fit its contents.
     */
    @Test
    public void testPack() {
        panel.setBounds(0, 0, 100, 100);
        panel.add(a);
        panel.pack(5);

        assertLayout(panel, 45, 45, 10, 10);
    }

    /**
     * Test whitespace-adding settings (insets and row/col spacing).
     */
    @Test
    public void testSpacing() {
        panel.setBounds(0, 0, 100, 100);
        panel.setInsets(20, 10, 20, 10);
        panel.setRowSpacing(4);
        panel.setColSpacing(2);

        panel.add(a).grow();
        panel.add(b).grow();
        panel.endRow();
        panel.add(c).grow();
        panel.add(d).grow();

        panel.validateLayout();

        assertLayout(a, 10, 20, 39, 28);
        assertLayout(b, 51, 20, 39, 28);
        assertLayout(c, 10, 52, 39, 28);
        assertLayout(d, 51, 52, 39, 28);
    }

    private void assertLayout(IVisualElement elem, int x, int y, int w, int h) {
        Area2D actual = elem.getLayoutAdapter().getLayoutBounds().toArea2D();
        RectAssert.assertEquals(Area2D.of(x, y, w, h), actual, 0.01);
    }

    private static IVisualElement image() {
        return CoreTestUtil.newImage(10, 10);
    }

}
