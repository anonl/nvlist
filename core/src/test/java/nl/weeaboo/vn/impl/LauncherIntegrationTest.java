package nl.weeaboo.vn.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.impl.test.integration.IntegrationTest;

@Category(GdxUiTest.class)
public class LauncherIntegrationTest extends IntegrationTest {

    @Test
    public void testFullScreenToggle() {
        Gdx.graphics.setUndecorated(true);
        Gdx.graphics.setWindowedMode(1, 1);
        assertFullScreen(false);

        InputProcessor input = Gdx.input.getInputProcessor();
        input.keyDown(Keys.ALT_LEFT);
        input.keyDown(Keys.ENTER);
        launcher.update();
        assertFullScreen(true);

        input.keyDown(Keys.ALT_LEFT);
        input.keyDown(Keys.ENTER);
        launcher.update();
        assertFullScreen(false);
    }

    @Test
    public void testResize() {
        // Nothing should explode when resized, even if the new size is 0x0
        launcher.resize(0, 0);
    }

    @Test
    public void testCloseRequested() {
        // Close requests are passed along to Lua, so the initial call returns false
        Assert.assertEquals(false, launcher.onCloseRequested());

        // If the launcher isn't running, there's no Lua context so we return true immediately
        launcher.dispose();
        Assert.assertEquals(true, launcher.onCloseRequested());
    }

    private static void assertFullScreen(boolean expected) {
        Assert.assertEquals(expected, Gdx.graphics.isFullscreen());
    }
}
