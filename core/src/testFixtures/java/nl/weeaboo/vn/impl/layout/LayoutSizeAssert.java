package nl.weeaboo.vn.impl.layout;

import org.junit.Assert;

import nl.weeaboo.vn.layout.LayoutSize;

final class LayoutSizeAssert {

    private static final double EPSILON = .001;

    public static void assertSize(LayoutSize expected, LayoutSize actual) {
        if (expected.isUnknown() || expected.isInfinite()) {
            Assert.assertEquals(expected, actual);
        } else {
            Assert.assertEquals(expected.value(), actual.value(Double.NaN), EPSILON);
        }
    }

}
