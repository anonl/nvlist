package nl.weeaboo.vn.impl.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.impl.core.AlignUtil;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.math.Vec2;

public class AlignUtilTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    @Test
    public void alignOffset() {
        Assert.assertEquals(0, AlignUtil.getAlignOffset(100, 0), EPSILON);
        Assert.assertEquals(-50, AlignUtil.getAlignOffset(100, .5), EPSILON);
        Assert.assertEquals(-100, AlignUtil.getAlignOffset(100, 1), EPSILON);
    }

    @Test
    public void alignSubRect() {
        Vec2 v = AlignUtil.alignSubRect(Rect2D.of(10, 10, 10, 10), 50, 40, Direction.BOTTOM_RIGHT);
        CoreTestUtil.assertEquals(30.0 / 50.0, 20.0 / 40.0, v, EPSILON);

        v = AlignUtil.alignSubRect(Rect2D.of(20, 5, 0, 15), 50, 40, Direction.BOTTOM_RIGHT);
        CoreTestUtil.assertEquals(30.0 / 50.0, 20.0 / 40.0, v, EPSILON);

        // Test all valid anchor positions
        Rect2D r = Rect2D.of(0, 0, 10, 10);
        assertAlign(.00, .50, r, 20, 20, Direction.BOTTOM_LEFT);
        assertAlign(.25, .50, r, 20, 20, Direction.BOTTOM);
        assertAlign(.50, .50, r, 20, 20, Direction.BOTTOM_RIGHT);
        assertAlign(.00, .25, r, 20, 20, Direction.LEFT);
        assertAlign(.25, .25, r, 20, 20, Direction.CENTER);
        assertAlign(.50, .25, r, 20, 20, Direction.RIGHT);
        assertAlign(.00, .00, r, 20, 20, Direction.TOP_LEFT);
        assertAlign(.25, .00, r, 20, 20, Direction.TOP);
        assertAlign(.50, .00, r, 20, 20, Direction.TOP_RIGHT);
    }

    private void assertAlign(double x, double y, Rect2D r, int outerW, int outerH, Direction dir) {
        CoreTestUtil.assertEquals(x, y, AlignUtil.alignSubRect(r, outerW, outerH, dir), EPSILON);
    }

}
