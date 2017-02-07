package nl.weeaboo.vn.impl.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

public class LUTInterpolatorTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    @Test
    public void basicTest() {
        IInterpolator source = Interpolators.LINEAR;
        // Create a LUT with a weird number of buckets to test interpolation between buckets
        LUTInterpolator lut = LUTInterpolator.fromInterpolator(source, 17);
        assertEquals(source, lut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberOfBuckets() {
        LUTInterpolator.fromInterpolator(Interpolators.LINEAR, 0);
    }

    @Test
    public void invalidX() {
        LUTInterpolator lut = LUTInterpolator.fromInterpolator(Interpolators.LINEAR, 2);

        // Out-of-range X is clamped to valid range: [0, 1]
        Assert.assertEquals(0, lut.remap(-1), EPSILON);
        Assert.assertEquals(1, lut.remap(1), EPSILON);
        Assert.assertEquals(0, lut.remap(Float.NEGATIVE_INFINITY), EPSILON);
        Assert.assertEquals(1, lut.remap(Float.POSITIVE_INFINITY), EPSILON);

        // NaN returns NaN
        Assert.assertEquals(Float.NaN, lut.remap(Float.NaN), EPSILON);
    }

    private static void assertEquals(IInterpolator expected, IInterpolator actual) {
        for (int n = 0; n <= 100; n++) {
            float f = n / 100f;
            Assert.assertEquals(expected.remap(f), actual.remap(f), EPSILON);
        }
    }

}
