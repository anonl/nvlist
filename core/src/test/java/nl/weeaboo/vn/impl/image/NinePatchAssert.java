package nl.weeaboo.vn.impl.image;

import org.junit.Assert;

import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.AreaId;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

public class NinePatchAssert {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    public static void assertEquals(INinePatch expected, INinePatch actual) {
        CoreTestUtil.assertEquals(expected.getInsets(), actual.getInsets());
        assertNativeSize(actual, expected.getNativeWidth(), expected.getNativeHeight());
        for (AreaId area : AreaId.values()) {
            Assert.assertEquals(expected.getTexture(area), actual.getTexture(area));
        }
    }

    public static void assertNativeSize(INinePatch ninePatch, double expectedW, double expectedH) {
        Assert.assertEquals(expectedW, ninePatch.getNativeWidth(), EPSILON);
        Assert.assertEquals(expectedH, ninePatch.getNativeHeight(), EPSILON);
    }
}
