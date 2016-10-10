package nl.weeaboo.vn.layout;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class LayoutSizeTest {

    private final LayoutSize s0 = LayoutSize.of(0);
    private final LayoutSize s1 = LayoutSize.of(1);
    private final LayoutSize s2 = LayoutSize.of(2);
    private final LayoutSize si = LayoutSize.INFINITE;
    private final LayoutSize su = LayoutSize.UNKNOWN;

    @Test
    public void positiveSizes() {
        Assert.assertSame(LayoutSize.ZERO, LayoutSize.of(+0.0));
        Assert.assertSame(LayoutSize.ZERO, LayoutSize.of(-0.0));

        Assert.assertEquals(123.0, LayoutSize.of(123).value(), 0.0);
    }

    @Test
    public void infiniteSize() {
        LayoutSize infinite = LayoutSize.of(Double.POSITIVE_INFINITY);
        Assert.assertSame(LayoutSize.INFINITE, infinite);
        Assert.assertEquals(true, infinite.isInfinite());
        Assert.assertEquals(false, infinite.isUnknown());
        assertValueUndefined(infinite);
    }

    @Test
    public void nanSize() {
        LayoutSize unknown = LayoutSize.of(Double.NaN);
        Assert.assertSame(LayoutSize.UNKNOWN, unknown);
        Assert.assertEquals(false, unknown.isInfinite());
        Assert.assertEquals(true, unknown.isUnknown());
        assertValueUndefined(unknown);
    }

    /** Negative sizes aren't allowed */
    @Test(expected = IllegalArgumentException.class)
    public void negativeValues() {
        LayoutSize.of(-1);
    }

    /** No negative values are allowed, but test for negative infinity specifically. */
    @Test(expected = IllegalArgumentException.class)
    public void negativeInfinity() {
        LayoutSize.of(Double.NEGATIVE_INFINITY);
    }

    /**
     * Tests for {@link LayoutSize#value(double)}.
     */
    @Test
    public void toDoubleWithDefault() {
        Assert.assertEquals(0.0, s0.value(Double.NaN), 0.0);
        Assert.assertEquals(1.0, s1.value(Double.NaN), 0.0);
        Assert.assertEquals(11.0, si.value(11.0), 0.0);
        Assert.assertEquals(22.0, su.value(22.0), 0.0);
    }

    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(s0, LayoutSize.ZERO)
            .addEqualityGroup(s1, LayoutSize.of(1.0))
            .addEqualityGroup(s2)
            .addEqualityGroup(si, LayoutSize.INFINITE)
            .addEqualityGroup(su, LayoutSize.UNKNOWN)
            .testEquals();
    }

    @Test
    public void testMinMax() {
        assertMin(s0, s1, s0);
        assertMax(s0, s1, s1);

        // Infinity is greater than all other known values
        for (LayoutSize size : Arrays.asList(s0, s1, si)) {
            assertMin(size, si, size);
            assertMax(size, si, si);
        }

        // Any comparison between a known an unknown value returns the known value
        for (LayoutSize size : Arrays.asList(s0, s1, si, su)) {
            assertMin(size, su, size);
            assertMax(size, su, size);
        }
    }

    private void assertMin(LayoutSize a, LayoutSize b, LayoutSize expectedResult) {
        Assert.assertSame(expectedResult, LayoutSize.min(a, b));
        Assert.assertSame(expectedResult, LayoutSize.min(b, a));
    }

    private void assertMax(LayoutSize a, LayoutSize b, LayoutSize expectedResult) {
        Assert.assertSame(expectedResult, LayoutSize.max(a, b));
        Assert.assertSame(expectedResult, LayoutSize.max(b, a));
    }

    private static void assertValueUndefined(LayoutSize size) {
        try {
            size.value();
            throw new AssertionError("Expected an exception for " + size);
        } catch (IllegalStateException ise) {
            // Expected
        }
    }

}
