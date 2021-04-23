package nl.weeaboo.vn.desktop;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.JsonValue;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;
import nl.weeaboo.vn.impl.save.JsonUtil;

@Category(GdxUiTest.class)
public class DesktopLauncherIntegrationTest extends DesktopIntegrationTest {

    @Test
    public void testLwjglConfig() {
        Lwjgl3ApplicationConfiguration config = launcher.createConfig(launcher.loadPreferences());

        // Lwjgl3ApplicationConfiguration has no public getters; (ab)use JSON mappers to check the private fields
        JsonValue json = JsonUtil.parse(JsonUtil.toJson(config));
        Assert.assertEquals(false, json.getBoolean("vSyncEnabled"));
        Assert.assertEquals("Pixels", json.getString("hdpiMode"));
        Assert.assertEquals("NVList (debug)", json.getString("title"));
        Assert.assertEquals(1280, json.getInt("windowWidth"));
        Assert.assertEquals(720, json.getInt("windowHeight"));
    }

    /**
     * You can override preferences via command-line options. The main use for this is to conditionally enable
     * debug mode.
     */
    @Test
    public void testCommandLineOptions() throws InitException {
        NovelPrefsStore prefs = launcher.loadPreferences();
        launcher.handleCommandlineOptions(prefs, new String[] {
                "-Pfullscreen=false"
        });
        Assert.assertEquals(false, prefs.get(NovelPrefs.FULLSCREEN));

        InitException ex = Assert.assertThrows(InitException.class, () -> {
            launcher.handleCommandlineOptions(prefs, new String[] {"-Punknown"});
        });
        Assert.assertEquals("Error parsing commandline options", ex.getMessage());
    }

}
