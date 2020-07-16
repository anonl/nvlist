package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

/** Test for tween.lua module */
public class LuaTweenTest extends LuaIntegrationTest {

    @Test
    public void testBitmapTweenIn() {
        loadScript("integration/tween/bitmap-tween-in");
    }

    @Test
    public void testBitmapTween() {
        loadScript("integration/tween/bitmap-tween");
    }

}
