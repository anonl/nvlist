
package nl.weeaboo.vn.math;

import org.junit.Assert;

import nl.weeaboo.vn.LvnTestUtil;

final class MathTestUtil {

    private MathTestUtil() {
    }

    public static void assertEquals(AbstractMatrix alpha, AbstractMatrix beta) {
        assertEquals(alpha, beta, LvnTestUtil.EPSILON);
    }
    public static void assertEquals(AbstractMatrix alpha, AbstractMatrix beta, double epsilon) {
        Assert.assertTrue(alpha + " != " + beta, alpha.equals(beta, epsilon));
    }

}
