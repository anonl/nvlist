package nl.weeaboo.vn.impl.layout;

import org.junit.Assert;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public final class LayoutTester {

    private static final double EPSILON = 0.001;

    public void layout(ILayoutGroup group, double width, double height) {
        group.setLayoutBounds(Rect2D.of(0, 0, width, height));
        group.layout();
    }

    public void assertBounds(ILayoutElem elem, double x, double y, double w, double h) {
        Rect2D bounds = elem.getLayoutBounds();
        Assert.assertEquals("Invalid x: " + bounds, x, bounds.x, EPSILON);
        Assert.assertEquals("Invalid y: " + bounds, y, bounds.y, EPSILON);
        assertSize(elem, w, h);
    }

    public void assertSize(ILayoutElem elem, double w, double h) {
        Rect2D bounds = elem.getLayoutBounds();
        Assert.assertEquals("Invalid w: " + bounds, w, bounds.w, EPSILON);
        Assert.assertEquals("Invalid h: " + bounds, h, bounds.h, EPSILON);
    }

    public void assertCalculatedHeight(ILayoutElem elem, double widthHint, LayoutSizeType type, LayoutSize expectedHeight) {
        LayoutSize actualHeight = elem.calculateLayoutHeight(type, LayoutSize.of(widthHint));
        LayoutSizeAssert.assertSize(expectedHeight, actualHeight);
    }

}
