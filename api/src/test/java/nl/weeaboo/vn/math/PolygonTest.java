package nl.weeaboo.vn.math;


import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.ApiTestUtil;

public class PolygonTest {

    private static final double EPSILON = ApiTestUtil.EPSILON;

    private final ExceptionTester exTester = new ExceptionTester();

    @Test
    public void boundsTest() {
        Rect2D r = Rect2D.of(1, 2, 3, 4);
        Polygon aligned = Polygon.transformedRect(Matrix.identityMatrix(), r);
        RectAssert.assertEquals(r, aligned.getBoundingRect(), EPSILON);

        // Empty polygon -> empty bounds
        Polygon p = new Polygon();
        RectAssert.assertEquals(Rect2D.EMPTY, p.getBoundingRect(), EPSILON);
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

        // If the polygon is a point or line, contains always returns false
        Assert.assertFalse(new Polygon(0, 0).contains(0, 0));
        Assert.assertFalse(new Polygon(0, 0, 1, 0).contains(0, 0));
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
                double x = -4 + 8 * random.nextDouble();
                double y = -4 + 8 * random.nextDouble();

                // Map point back to rectangle coordinates
                Vec2 rv = inverseTransform.transform(x, y);

                Assert.assertEquals("x=" + x + ", y=" + y,
                        rect.contains(rv.x, rv.y), polygon.contains(x, y));
            }
        }
    }

    /**
     * It's possible to supply different numbers of X and Y-coordinates to the constructor. This should
     * trigger an exception.
     */
    @Test
    public void testMismatchedCoordinates() {
        exTester.expect(IllegalArgumentException.class, () -> new Polygon(0));
        exTester.expect(IllegalArgumentException.class, () -> new Polygon(new double[0], new double[1]));
    }

    /**
     * Test the polygon's behavior when some of its coordinates are NaN or Infinite.
     */
    @Test
    public void testNonFiniteCoordinates() {
        exTester.expect(IllegalArgumentException.class, () -> new Polygon(0, Double.NaN));
        exTester.expect(IllegalArgumentException.class, () -> new Polygon(0, Double.NEGATIVE_INFINITY));
        exTester.expect(IllegalArgumentException.class, () -> new Polygon(0, Double.POSITIVE_INFINITY));
    }

}
