package nl.weeaboo.vn.core.impl;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.script.impl.lua.LuaTestUtil;

public class SeenLogTest {

    private TestEnvironment env;
    private SeenLog seenLog;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();
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

    @Test
    public void testPersist() throws IOException {
        // Add a resource for each type
        for (MediaType type : MediaType.values()) {
            add("x", type);
        }

        String filename = LuaTestUtil.SCRIPT_HELLOWORLD;
        ResourceId fileId = new ResourceId(MediaType.SCRIPT, filename);
        seenLog.registerScriptFile(fileId, 5);
        seenLog.markLineSeen(filename, 1);
        seenLog.markLineSeen(filename, 3);
        seenLog.markLineSeen(filename, 5);

        // Store current state
        SecureFileWriter sfw = new SecureFileWriter(env.getOutputFileSystem());
        seenLog.save(sfw, "seen.bin");

        // Add some additional resources after saving
        for (MediaType type : MediaType.values()) {
            add("y", type);
        }
        seenLog.markLineSeen(filename, 2);
        seenLog.markLineSeen(filename, 4);

        // Load previously stored state. The extra resources should no longer be contained
        seenLog.load(sfw, "seen.bin");
        for (MediaType type : MediaType.values()) {
            assertContains(true, "x", type);
            assertContains(false, "y", type);
        }
        assertLineSeen(true, filename, 1);
        assertLineSeen(false, filename, 2);
        assertLineSeen(true, filename, 3);
        assertLineSeen(false, filename, 4);
        assertLineSeen(true, filename, 5);
    }

    @Test
    public void scriptLinesSeen() {
        String filename = LuaTestUtil.SCRIPT_HELLOWORLD;
        ResourceId fileId = new ResourceId(MediaType.SCRIPT, filename);

        seenLog.markLineSeen(filename, 1); // Script file not registered (yet)
        assertLineSeen(false, filename, 1);

        seenLog.registerScriptFile(fileId, 5);

        seenLog.markLineSeen(filename, 0); // Out of range (line numbers start at 1)
        assertLineSeen(false, filename, 0);

        seenLog.markLineSeen(filename, 6); // Out of range (only 5 lines in script)
        assertLineSeen(false, filename, 6);

        seenLog.markLineSeen(filename, 3);
        assertLineSeen(true, filename, 3);

        // Re-register script file (unchanged)
        seenLog.registerScriptFile(fileId, 5);
        assertLineSeen(true, filename, 3);

        // Re-register script file (changed)
        seenLog.registerScriptFile(fileId, 6);
        assertLineSeen(false, filename, 3); // Lines seen was cleared
    }

    private void assertLineSeen(boolean expected, String filename, int lineNum) {
        Assert.assertEquals(expected, seenLog.hasSeenLine(filename, lineNum));
    }

    private void assertContains(boolean expected, String fn) {
        assertContains(expected, fn, MediaType.OTHER);
    }
    private void assertContains(boolean expected, String fn, MediaType mediaType) {
        Assert.assertEquals(expected, seenLog.hasSeen(new ResourceId(mediaType, fn)));
    }

    private boolean add(String fn) {
        return add(fn, MediaType.OTHER);
    }
    private boolean add(String fn, MediaType mediaType) {
        return seenLog.markSeen(new ResourceId(mediaType, fn));
    }

}
