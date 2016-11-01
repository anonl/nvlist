package nl.weeaboo.vn.input;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;

import nl.weeaboo.gdx.test.ExceptionTester;

public class VKeyTest {

    /**
     * Because the id strings are stored in various config files, check that they don't change unexpectedly
     * between releases.
     */
    @Test
    public void testStringIdsUnchanged() {
        assertId("up", VKey.UP);
        assertId("down", VKey.DOWN);
        assertId("left", VKey.LEFT);
        assertId("right", VKey.RIGHT);

        assertId("confirm", VKey.CONFIRM);
        assertId("cancel", VKey.CANCEL);
        assertId("textContinue", VKey.TEXT_CONTINUE);
        assertId("skip", VKey.SKIP);

        assertId("mouseLeft", VKey.MOUSE_LEFT);

        // Ensure we've checked all standard key mappings
        Assert.assertEquals(9, Iterables.size(VKey.getStandardKeys()));
    }

    @Test
    public void testFromString() {
        ExceptionTester exTester = new ExceptionTester();
        // Null ID is invalid
        exTester.expect(IllegalArgumentException.class, () -> VKey.fromString(null));
        // Empty ID is valid
        Assert.assertEquals("", VKey.fromString("").getId());
    }

    private void assertId(String expected, VKey key) {
        Assert.assertEquals(expected, key.getId());
    }

}
