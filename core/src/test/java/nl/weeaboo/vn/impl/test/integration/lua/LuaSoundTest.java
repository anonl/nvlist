package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

/** Test for sound.lua module */
public class LuaSoundTest extends LuaIntegrationTest {

    @Test
    public void testMusicStartStop() {
        loadScript("integration/sound/music");
        waitForAllThreads();
    }

    @Test
    public void testMusicFadeTime() {
        loadScript("integration/sound/musicfadetime");
        waitForAllThreads();
    }

}