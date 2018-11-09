package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.image.IBitmapTweenConfig;
import nl.weeaboo.vn.image.IBitmapTweenRenderer;
import nl.weeaboo.vn.image.ICrossFadeConfig;
import nl.weeaboo.vn.image.ICrossFadeRenderer;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class TweenLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new TweenLib(env));
    }

    @Test
    public void testCrossFade() {
        loadScript("integration/tween/cross-fade");

        ICrossFadeConfig config = LuaTestUtil.getGlobal("config", ICrossFadeConfig.class);
        Assert.assertEquals(123, config.getDuration(), 1e-3);

        ICrossFadeRenderer fade = LuaTestUtil.getGlobal("fade", ICrossFadeRenderer.class);
        Assert.assertNotNull(fade);
    }

    @Test
    public void testBitmapTween() {
        loadScript("integration/tween/bitmap-tween");

        IBitmapTweenConfig config = LuaTestUtil.getGlobal("config", IBitmapTweenConfig.class);
        Assert.assertEquals(123, config.getDuration(), 1e-3);

        IBitmapTweenRenderer tween = LuaTestUtil.getGlobal("tween", IBitmapTweenRenderer.class);
        Assert.assertNotNull(tween);
    }

}
