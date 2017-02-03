package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.ApiTestUtil;

public class InterpolatorsTest {

    @Test
    public void testLinear() {
        testInterpolator(new double[] {
                0.0,
                0.1,
                0.2,
                0.3,
                0.4,
                0.5,
                0.6,
                0.7,
                0.8,
                0.9,
                1.0
        }, Interpolators.LINEAR);
    }

    @Test
    public void testHermite() {
        testInterpolator(new double[] {
                0.000,
                0.028f,
                0.104f,
                0.216f,
                0.352f,
                0.500f,
                0.648f,
                0.784f,
                0.896f,
                0.972f,
                1.000f
        }, Interpolators.HERMITE);
    }

    private static void testInterpolator(double[] expected, IInterpolator interpolator) {
        for (int n = 0; n < expected.length; n++) {
            float f = n / (float)(expected.length - 1);
            Assert.assertEquals(expected[n], interpolator.remap(f), ApiTestUtil.EPSILON);
        }
    }

}
