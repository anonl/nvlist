package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

/** Test for image.lua module */
public class LuaImageTest extends LuaIntegrationTest {

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

    @Test
    public void testLayerFunctions() {
        loadScript("integration/image/layer");
    }
}
