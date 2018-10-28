package nl.weeaboo.vn.impl.save;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.vn.core.Duration;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.stats.IPlayTimer;
import nl.weeaboo.vn.stats.IStatsModule;

public final class SaveModuleTest {

    private final ExceptionTester exTester = new ExceptionTester();

    private TestEnvironment env;
    private IPlayTimer playTimer;
    private SaveModule saveModule;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();

        IStatsModule statsModule = env.getStatsModule();
        playTimer = statsModule.getPlayTimer();
        playTimer.update(); // Initializes the internal timestamp so it starts measuring time

        saveModule = new SaveModule(env);
    }

    @After
    public void after() {
        env.destroy();
    }

    @Test
    public void testSavePersistent() {
        // Attempting to load when there's no data yet shouldn't throw an exception
        saveModule.loadPersistent();

        // Update play timer so it has a non-zero play-time value
        playTimer.update();
        Duration time = playTimer.getTotalPlayTime();
        Assert.assertNotEquals(Duration.ZERO, time);

        // Save data (including the play timer)
        saveModule.savePersistent();

        // Allow the play timer to increase some more, so we can show that loading restores the saved value
        playTimer.update();
        Assert.assertNotEquals(time, playTimer.getTotalPlayTime());

        // Load persistent data again (including the play timer)
        saveModule.loadPersistent();
        Assert.assertEquals(time, playTimer.getTotalPlayTime());
    }

    @Test
    public void testSpecialSaveSlots() {
        assertQuickSaveRange(801, 899);
        Assert.assertEquals(801, saveModule.getQuickSaveSlot(1));
        Assert.assertEquals(899, saveModule.getQuickSaveSlot(99));
        exTester.expect(IllegalArgumentException.class, () -> saveModule.getQuickSaveSlot(0));
        exTester.expect(IllegalArgumentException.class, () -> saveModule.getQuickSaveSlot(100));

        assertAutoSaveRange(901, 999);
        Assert.assertEquals(901, saveModule.getAutoSaveSlot(1));
        Assert.assertEquals(999, saveModule.getAutoSaveSlot(99));
        exTester.expect(IllegalArgumentException.class, () -> saveModule.getAutoSaveSlot(0));
        exTester.expect(IllegalArgumentException.class, () -> saveModule.getAutoSaveSlot(100));
    }

    private void assertQuickSaveRange(int min, int max) {
        for (int slot = 0; slot < 9_999; slot++) {
            Assert.assertEquals(Integer.toString(slot), slot >= min && slot <= max, SaveModule.isQuickSaveSlot(slot));
        }
    }

    private void assertAutoSaveRange(int min, int max) {
        for (int slot = 0; slot < 9_999; slot++) {
            Assert.assertEquals(Integer.toString(slot), slot >= min && slot <= max, SaveModule.isAutoSaveSlot(slot));
        }
    }

}
