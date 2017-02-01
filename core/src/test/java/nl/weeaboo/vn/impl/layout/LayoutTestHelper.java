package nl.weeaboo.vn.impl.layout;

import org.junit.Assert;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.layout.DummyLayoutElem;
import nl.weeaboo.vn.layout.ILayoutGroup;

final class LayoutTestHelper {

    private static final double EPSILON = 0.001;

    private ILayoutGroup layout;

    public LayoutTestHelper(ILayoutGroup layout) {
        this.layout = layout;
    }

    public void layout(double width, double height) {
        layout.setLayoutBounds(Rect2D.of(0, 0, width, height));
        layout.layout();
    }

    public void assertBounds(DummyLayoutElem elem, double x, double y, double w, double h) {
        Rect2D bounds = elem.getLayoutBounds();
        Assert.assertEquals("Invalid x: " + bounds, x, bounds.x, EPSILON);
        Assert.assertEquals("Invalid y: " + bounds, y, bounds.y, EPSILON);
        assertSize(elem, w, h);
    }

    public void assertSize(DummyLayoutElem elem, double w, double h) {
        Rect2D bounds = elem.getLayoutBounds();
        Assert.assertEquals("Invalid w: " + bounds, w, bounds.w, EPSILON);
        Assert.assertEquals("Invalid h: " + bounds, h, bounds.h, EPSILON);
    }

}
