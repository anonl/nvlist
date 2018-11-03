package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public final class VnIdTest {

    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(id("aa"), id("aa"))
            .addEqualityGroup(id("bb"))
            .testEquals();
    }

    /**
     * Test for valid ID formats.
     */
    @Test
    public void testValidIds() {
        assertValid("abc");
        assertValid("abc_123");

        assertNotValid("");
        assertNotValid("a");  // ID must be at least two letters long
        assertNotValid("aé"); // Only ASCII letters allowed
        assertNotValid("aB"); // Only lowercase letters allowed
        assertNotValid("0x"); // ID must start with a letter
        assertNotValid("_x"); // ID must start with a letter
    }

    private static void assertValid(String id) {
        Assert.assertEquals(true, VnId.isValidId(id));
    }

    private static void assertNotValid(String id) {
        Assert.assertEquals(false, VnId.isValidId(id));
    }

    private static final VnId id(String id) {
        return new VnId(id);
    }

}
