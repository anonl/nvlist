package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class SkipModeTest {

    @Test
    public void testOrder() {
        for (SkipMode mode : SkipMode.values()) {
            // max(a,a) = a
            Assert.assertEquals(mode, SkipMode.max(mode, mode));
            // max(a,null) = a
            Assert.assertEquals(mode, SkipMode.max(mode, null));
            Assert.assertEquals(mode, SkipMode.max(null, mode));
        }
        // max(null,null) = null
        Assert.assertEquals(null, SkipMode.max(null, null));

        // Check that all comparisons are possible (no IncomparableValueException thrown)
        for (SkipMode a : SkipMode.values()) {
            for (SkipMode b : SkipMode.values()) {
                SkipMode.max(a, b);
            }
        }
    }

}
