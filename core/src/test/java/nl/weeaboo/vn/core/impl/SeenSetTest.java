package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.ResourceId;

public class SeenSetTest {

    private SeenSet seen;

    @Before
    public void before() {
        seen = new SeenSet();
    }

    /** Test behavior when adding only a few items */
    @Test
    public void addSeveral() {
        add("a");
        add("b");
        add("c");

        assertContains(true, "a");
        assertContains(true, "b");
        assertContains(true, "c");
        assertContains(false, "d");
    }

    /** Test behavior when adding way too many items */
    @Test
    public void addMany() {
        int totalAdded = 0;
        final int itemCount = 1000000;
        for (int n = 0; n < itemCount; n++) {
            boolean added = add(Integer.toHexString(n));
            if (added) {
                totalAdded++;
            }
        }

        // With these settings, the error rate is one in a million
        Assert.assertEquals(itemCount, totalAdded);
    }

    private void assertContains(boolean expected, String fn) {
        Assert.assertEquals(expected, seen.probablyContains(new ResourceId(fn)));
    }

    private boolean add(String fn) {
        return seen.add(new ResourceId(fn));
    }

}
