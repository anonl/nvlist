package nl.weeaboo.vn.impl.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class SeenLogTest {

    private static final String CHOICE_A = "choiceA";
    private static final String CHOICE_B = "choiceB";

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
        final FilePath pathA = FilePath.of("a");
        final FilePath pathB = FilePath.of("b");
        final FilePath pathC = FilePath.of("c");
        final FilePath pathD = FilePath.of("d");

        add(pathA);
        add(pathB);
        add(pathC);

        assertContains(true, pathA);
        assertContains(true, pathB);
        assertContains(true, pathC);
        assertContains(false, pathD);
    }

    /** Test behavior when adding way too many items */
    @Test
    public void addMany() {
        int totalAdded = 0;
        final int itemCount = 100000;
        for (int n = 0; n < itemCount; n++) {
            boolean added = add(FilePath.of(Integer.toHexString(n)));
            if (added) {
                totalAdded++;
            }
        }

        // With these settings, the error rate is one in a million
        Assert.assertEquals(itemCount, totalAdded);
    }

    @Test
    public void testPersist() throws IOException {
        FilePath pathX = FilePath.of("x");
        FilePath pathY = FilePath.of("y");

        // Add a resource for each type
        for (MediaType type : MediaType.values()) {
            add(pathX, type);
        }

        // Mark some script lines as seen
        FilePath filename = LuaTestUtil.SCRIPT_HELLOWORLD;
        ResourceId fileId = new ResourceId(MediaType.SCRIPT, filename);
        seenLog.registerScriptFile(fileId, 5);
        seenLog.markLineSeen(filename, 1);
        seenLog.markLineSeen(filename, 3);
        seenLog.markLineSeen(filename, 5);

        // Mark some choices as seen
        seenLog.registerChoice(CHOICE_A, 3);
        seenLog.markChoiceSelected(CHOICE_A, 2); // Note: index is 1-based
        seenLog.registerChoice(CHOICE_B, 2);
        seenLog.markChoiceSelected(CHOICE_B, 1);

        // Store current state
        SecureFileWriter sfw = new SecureFileWriter(env.getOutputFileSystem());
        FilePath seenFile = FilePath.of("seen.bin");
        seenLog.save(sfw, seenFile);

        // Add some additional resources after saving
        for (MediaType type : MediaType.values()) {
            add(pathY, type);
        }
        seenLog.markLineSeen(filename, 2);
        seenLog.markLineSeen(filename, 4);

        // Load previously stored state. The extra resources should no longer be contained
        seenLog.load(sfw, seenFile);
        for (MediaType type : MediaType.values()) {
            assertContains(true, pathX, type);
            assertContains(false, pathY, type);
        }

        // Check script lines seen
        assertLineSeen(true, filename, 1);
        assertLineSeen(false, filename, 2);
        assertLineSeen(true, filename, 3);
        assertLineSeen(false, filename, 4);
        assertLineSeen(true, filename, 5);

        // Check choices seen
        assertChoicesSelected(CHOICE_A, false, true, false);
        assertChoicesSelected(CHOICE_B, true, false);
    }

    @Test
    public void scriptLinesSeen() {
        FilePath filename = LuaTestUtil.SCRIPT_HELLOWORLD;
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

    @Test
    public void choicesSeen() {
        // Double-register choice
        seenLog.registerChoice(CHOICE_A, 3);
        seenLog.registerChoice(CHOICE_A, 3);

        // Ask if an out-of-range choice was selected
        Assert.assertFalse(seenLog.hasSelectedChoice(CHOICE_A, 4));

        // Ask if an unregistered choice was selected
        Assert.assertFalse(seenLog.hasSelectedChoice(CHOICE_B, 1));
    }

    private void assertLineSeen(boolean expected, FilePath filename, int lineNum) {
        Assert.assertEquals(expected, seenLog.hasSeenLine(filename, lineNum));
    }

    private void assertContains(boolean expected, FilePath fn) {
        assertContains(expected, fn, MediaType.OTHER);
    }

    private void assertContains(boolean expected, FilePath fn, MediaType mediaType) {
        Assert.assertEquals(expected, seenLog.hasSeen(new ResourceId(mediaType, fn)));
    }

    private void assertChoicesSelected(String choiceId, boolean... expected) {
        for (int n = 0; n < expected.length; n++) {
            Assert.assertEquals("Index " + n,
                    expected[n], seenLog.hasSelectedChoice(choiceId, n + 1));
        }
    }

    private boolean add(FilePath fn) {
        return add(fn, MediaType.OTHER);
    }

    private boolean add(FilePath fn, MediaType mediaType) {
        return seenLog.markSeen(new ResourceId(mediaType, fn));
    }

}
