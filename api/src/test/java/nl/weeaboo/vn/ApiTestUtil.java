package nl.weeaboo.vn;

import org.junit.Assert;

import nl.weeaboo.vn.math.Vec2;

public final class ApiTestUtil {

    public static final double EPSILON = 0.001;

    private ApiTestUtil() {
    }

    /**
     * Fuzzy equals for {@link Vec2}.
     */
    public static void assertEquals(double x, double y, Vec2 vec, double epsilon) {
        Assert.assertEquals(x, vec.x, epsilon);
        Assert.assertEquals(y, vec.y, epsilon);
    }

}
