package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;

public class SeenLogTest {

    private SeenLog seenLog;

    @Before
    public void before() {
        TestEnvironment env = TestEnvironment.newInstance();
        seenLog = new SeenLog(env);
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
        final int itemCount = 100000;
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
        Assert.assertEquals(expected, seenLog.hasSeen(new ResourceId(MediaType.OTHER, fn)));
    }

    private boolean add(String fn) {
        return seenLog.markSeen(new ResourceId(MediaType.OTHER, fn));
    }

}
