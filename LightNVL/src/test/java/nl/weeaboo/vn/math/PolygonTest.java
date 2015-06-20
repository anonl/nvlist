package nl.weeaboo.vn.math;


import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.LvnTestUtil;

import org.junit.Assert;
import org.junit.Test;

public class PolygonTest {

    @Test
    public void boundsTest() {
        Rect2D r = Rect2D.of(1, 2, 3, 4);
        Polygon aligned = Polygon.rotatedRect(Matrix.identityMatrix(), r.x, r.y, r.w, r.h);
        LvnTestUtil.assertEquals(r, aligned.getBoundingRect());

        // Bounds when one or more coords are NaN
        Polygon nanPoly = Polygon.rotatedRect(Matrix.identityMatrix(), r.x, Double.NaN, r.w, r.h);
        LvnTestUtil.assertEquals(Rect2D.EMPTY, nanPoly.getBoundingRect());
        Assert.assertFalse(nanPoly.contains(0, 0)); // Bound w/h are exclusive
    }

    @Test
    public void containsTest() {
        Rect2D r = Rect2D.of(1, 2, 3, 4);
        Polygon aligned = Polygon.rotatedRect(Matrix.identityMatrix(), r.x, r.y, r.w, r.h);

        Assert.assertTrue(aligned.contains(1, 2)); //Top-left corner
        Assert.assertTrue(aligned.contains(1, 5.99)); //Bottom-left corner
        Assert.assertTrue(aligned.contains(3.99, 4)); //Right edge

        Assert.assertFalse(aligned.contains(0, 0)); // Outside top-left
        Assert.assertFalse(aligned.contains(0, 4)); //Outside left
        Assert.assertFalse(aligned.contains(5, 4)); //Outside right
        Assert.assertFalse(aligned.contains(2, 7)); //Outside bottom
    }

}
