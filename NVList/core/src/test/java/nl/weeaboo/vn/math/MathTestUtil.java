package nl.weeaboo.vn.math;

import nl.weeaboo.vn.TestUtil;

import org.junit.Assert;

final class MathTestUtil {

    private MathTestUtil() {
    }

    public static void assertEquals(AbstractMatrix alpha, AbstractMatrix beta) {
        assertEquals(alpha, beta, TestUtil.EPSILON);
    }
    public static void assertEquals(AbstractMatrix alpha, AbstractMatrix beta, double epsilon) {
        Assert.assertTrue(alpha + " != " + beta, alpha.equals(beta, epsilon));
    }

}
