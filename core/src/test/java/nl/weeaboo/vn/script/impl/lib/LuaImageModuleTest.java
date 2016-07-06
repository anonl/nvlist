package nl.weeaboo.vn.script.impl.lib;

import org.junit.Test;

import nl.weeaboo.vn.test.integration.LuaIntegrationTest;

/** Test for image.lua module */
public class LuaImageModuleTest extends LuaIntegrationTest {

    @Test
    public void testBackgroundFunctions() {
        loadScript("integration/image/background");
    }

    @Test
    public void testImageFunctions() {
        loadScript("integration/image/image");
    }

    /** Each context should have separate image state*/
    @Test
    public void imageStatesInConcurrentContexts() {
        loadScript("integration/image/multicontext");
    }
}
