package nl.weeaboo.vn.math;


import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.ApiTestUtil;

public class PolygonTest {

    private static final double EPSILON = ApiTestUtil.EPSILON;

    @Test
    public void boundsTest() {
        Rect2D r = Rect2D.of(1, 2, 3, 4);
        Polygon aligned = Polygon.transformedRect(Matrix.identityMatrix(), r);
        RectAssert.assertEquals(r, aligned.getBoundingRect(), EPSILON);

        // Bounds when one or more coords are NaN
        Polygon nanPoly = Polygon.transformedRect(Matrix.translationMatrix(Double.NaN, 0), r);
        RectAssert.assertEquals(Rect2D.EMPTY, nanPoly.getBoundingRect(), EPSILON);
        Assert.assertFalse(nanPoly.contains(0, 0)); // Bound w/h are exclusive
    }

    @Test
    public void containsTest() {
        Rect2D r = Rect2D.of(1, 2, 3, 4);
        Polygon aligned = Polygon.transformedRect(Matrix.identityMatrix(), r);

        Assert.assertTrue(aligned.contains(1, 2)); //Top-left corner
        Assert.assertTrue(aligned.contains(1, 5.99)); //Bottom-left corner
        Assert.assertTrue(aligned.contains(3.99, 4)); //Right edge

        Assert.assertFalse(aligned.contains(0, 0)); // Outside top-left
        Assert.assertFalse(aligned.contains(0, 4)); //Outside left
        Assert.assertFalse(aligned.contains(5, 4)); //Outside right
        Assert.assertFalse(aligned.contains(2, 7)); //Outside bottom
    }


    @Test
    public void randomContainsTest() {
        // Calculate length of sides to get a diagonal of length 2
        double s = Math.sqrt(2 * 2 + 2 * 2);

        Random random = new Random(12345);
        for (int angle = 0; angle < 256; angle++) {
            Rect2D rect = Rect2D.of(-s, -s, 2 * s, 2 * s);
            Matrix transform = Matrix.rotationMatrix(angle);
            Matrix inverseTransform = Matrix.rotationMatrix(-angle);
            Polygon polygon = Polygon.transformedRect(transform, rect);

            // Check using random points that the polygon and equivalent square return equal results for contains
            for (int n = 0; n < 1000; n++) {
                double x = -1 + 2 * random.nextDouble();
                double y = -1 + 2 * random.nextDouble();

                // Map point back to rectangle coordinates
                Vec2 rv = inverseTransform.transform(x, y);

                Assert.assertEquals("x=" + x + ", y=" + y,
                        rect.contains(rv.x, rv.y), polygon.contains(x, y));
            }
        }
    }
}
