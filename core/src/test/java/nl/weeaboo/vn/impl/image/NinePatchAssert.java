package nl.weeaboo.vn.impl.image;

import org.junit.Assert;

import nl.weeaboo.test.InsetsAssert;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.AreaId;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

public class NinePatchAssert {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    /**
     * Compares two nine-patches for equality.
     * @throws AssertionError If the two nine-patches aren't equal to each other.
     */
    public static void assertEquals(INinePatch expected, INinePatch actual) {
        InsetsAssert.assertEquals(expected.getInsets(), actual.getInsets(), EPSILON);
        assertNativeSize(actual, expected.getNativeWidth(), expected.getNativeHeight());
        for (AreaId area : AreaId.values()) {
            Assert.assertEquals(expected.getTexture(area), actual.getTexture(area));
        }
    }

    /**
     * Checks that the native width/height of a nine-patch match the expected values.
     * @throws AssertionError If the native size of the nine-patch doesn't have the expected value.
     */
    public static void assertNativeSize(INinePatch ninePatch, double expectedW, double expectedH) {
        Assert.assertEquals(expectedW, ninePatch.getNativeWidth(), EPSILON);
        Assert.assertEquals(expectedH, ninePatch.getNativeHeight(), EPSILON);
    }
}
