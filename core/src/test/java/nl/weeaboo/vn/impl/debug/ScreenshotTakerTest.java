package nl.weeaboo.vn.impl.debug;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.input.GdxInputRobot;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.test.integration.render.RenderIntegrationTest;

@Category(GdxUiTest.class)
public class ScreenshotTakerTest extends RenderIntegrationTest {

    private IWritableFileSystem outputFileSystem;
    private GdxInputRobot input;

    @Before
    public void before() {
        outputFileSystem = StaticEnvironment.OUTPUT_FILE_SYSTEM.get();

        input = new GdxInputRobot(Gdx.input.getInputProcessor());
    }

    @Test
    public void test() {
        // Screenshot functionality is only available in debug mode
        input.type(Keys.F12);
        handleInputs();
        assertScreenshots(0);

        setDebugMode(true);

        // Press the screenshot key -> screenshot is written to the save folder
        input.type(Keys.F12);
        handleInputs();
        assertScreenshots(1);
    }

    private void handleInputs() {
        launcher.update(); // Read input
        launcher.render(); // Take screenshot if needed
        launcher.update(); // Write screenshot if available
    }

    private void assertScreenshots(int expectedCount) {
        FileCollectOptions filter = FileCollectOptions.files(FilePath.empty(), "screenshot");
        ImmutableSet<FilePath> screenshotFiles = ImmutableSet.copyOf(outputFileSystem.getFiles(filter));
        Assert.assertEquals(expectedCount, screenshotFiles.size());
    }

    private void setDebugMode(boolean enabled) {
        IPreferenceStore prefStore = getEnv().getPrefStore();
        prefStore.set(NovelPrefs.DEBUG, enabled);
    }

}
