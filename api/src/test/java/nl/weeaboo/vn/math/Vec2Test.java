package nl.weeaboo.vn.math;

import static nl.weeaboo.test.SerializeTester.deserializeObject;
import static nl.weeaboo.test.SerializeTester.serializeObject;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.ApiTestUtil;

public class Vec2Test {

    private static final double E = ApiTestUtil.EPSILON;

    @Test
    public void vectorTest() {
        Vec2 a = new Vec2(1, 2);
        Vec2 b = new Vec2(4, 3);

        // Copy constructor
        ApiTestUtil.assertEquals(a.x, a.y, new Vec2(a), 0);
        Assert.assertEquals(a.hashCode(), new Vec2(a).hashCode());

        // Dot product
        Assert.assertEquals(10, a.dot(b), E);

        // Add, sub, scale
        a.add(b);
        ApiTestUtil.assertEquals(5, 5, a, E);
        a.sub(b);
        ApiTestUtil.assertEquals(1, 2, a, E);
        a.scale(-.5);
        ApiTestUtil.assertEquals(-.5, -1, a, E);
    }

    @Test
    public void testEquals() {
        Vec2 a = new Vec2(1, 2);
        Vec2 b = new Vec2(4, 3);

        Assert.assertNotEquals(a, null);

        // Equals
        Assert.assertEquals(a, a);
        Assert.assertNotEquals(a, new Vec2(1, 3));
        Assert.assertNotEquals(a, new Vec2(3, 2));
        Assert.assertNotEquals(a, b);

        // Fuzzy equals
        Assert.assertTrue(a.equals(a, E));
        Assert.assertFalse(a.equals(new Vec2(1, 3), E));
        Assert.assertTrue(a.equals(new Vec2(1, 3), 1.0));
        Assert.assertFalse(a.equals(new Vec2(3, 2), E));
        Assert.assertTrue(a.equals(new Vec2(3, 2), 2.0));
        Assert.assertFalse(a.equals(b, E));
    }

    @Test
    public void length() {
        Vec2 a = new Vec2(1, 2);
        Vec2 b = new Vec2(4, 3);

        ApiTestUtil.assertEquals(a.y - b.y, b.x - a.x, a.cross(b), 0);

        Assert.assertEquals(25, b.lengthSquared(), E);
        Assert.assertEquals(5, b.length(), E);

        b.normalize();
        ApiTestUtil.assertEquals(4.0 / 5.0, 3.0 / 5.0, b, E);
    }

    @Test
    public void vectorSerializeTest() {
        Vec2 vec = new Vec2(1, 2);
        checkSerialize(vec);

        vec = new Vec2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        checkSerialize(vec);

        vec = new Vec2(Double.MIN_VALUE, Double.MIN_NORMAL);
        checkSerialize(vec);

        // Since Double.NaN != Double.Nan, we need to use an epsilon here
        vec = new Vec2(Double.NaN, 0.0);
        checkSerialize(vec, E);
        vec = new Vec2(0.0, Double.NaN);
        checkSerialize(vec, E);
    }

    /** Check that serialialization doesn't discard any information (object is still equal to itself) */
    private void checkSerialize(Vec2 vec) {
        checkSerialize(vec, 0.0);
    }

    private void checkSerialize(Vec2 vec, double epsilon) {
        String message = "Vec: " + vec;

        Vec2 serialized = deserializeObject(serializeObject(vec), Vec2.class);
        if (epsilon == 0.0) {
            Assert.assertEquals(message, vec, serialized);
        } else {
            Assert.assertTrue(message, vec.equals(serialized, epsilon));
        }
    }

}
