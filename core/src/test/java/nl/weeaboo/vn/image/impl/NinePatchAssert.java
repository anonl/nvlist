package nl.weeaboo.vn.image.impl;

import org.junit.Assert;

import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.EArea;
import nl.weeaboo.vn.test.CoreTestUtil;

public class NinePatchAssert {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    public static void assertEquals(INinePatch expected, INinePatch actual) {
        CoreTestUtil.assertEquals(expected.getInsets(), actual.getInsets());
        assertNativeSize(actual, expected.getNativeWidth(), expected.getNativeHeight());
        for (EArea area : EArea.values()) {
            Assert.assertEquals(expected.getTexture(area), actual.getTexture(area));
        }
    }

    public static void assertNativeSize(INinePatch ninePatch, double expectedW, double expectedH) {
        Assert.assertEquals(expectedW, ninePatch.getNativeWidth(), EPSILON);
        Assert.assertEquals(expectedH, ninePatch.getNativeHeight(), EPSILON);
    }
}
