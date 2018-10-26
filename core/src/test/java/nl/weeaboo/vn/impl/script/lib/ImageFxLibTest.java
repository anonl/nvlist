package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.fx.BlurTask;
import nl.weeaboo.vn.impl.render.fx.ColorMatrixTask;
import nl.weeaboo.vn.impl.render.fx.ImageCompositeTask;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class ImageFxLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new ImageFxLib(env));
    }

    @Ignore
    @Test
    public void testCrop() {
        loadScript("integration/imagefx/crop");

        ITexture cropped = LuaTestUtil.getGlobal("cropped").touserdata(ITexture.class);
        Assert.assertEquals(Area2D.of(0, 0, 0, 0), cropped.getUV());
    }

    @Test
    public void testBlur() {
        loadScript("integration/imagefx/blur");

        LuaTestUtil.assertGlobal("nilBlur", null);

        LuaTestUtil.getGlobal("blur10").checkuserdata(BlurTask.class);
    }

    @Test
    public void testBrighten() {
        loadScript("integration/imagefx/brighten");

        LuaTestUtil.assertGlobal("nilBrighten", null);

        LuaTestUtil.getGlobal("brighten").checkuserdata(ColorMatrixTask.class);
    }

    @Test
    public void testColorMatrix() {
        loadScript("integration/imagefx/colormatrix");

        LuaTestUtil.assertGlobal("nilTexture", null);

        LuaTestUtil.getGlobal("colorMatrix").checkuserdata(ColorMatrixTask.class);
    }

    @Test
    public void testComposite() {
        loadScript("integration/imagefx/composite");

        LuaTestUtil.getGlobal("missingTexture").checkuserdata(ImageCompositeTask.class);
        LuaTestUtil.getGlobal("composite").checkuserdata(ImageCompositeTask.class);
    }
}
