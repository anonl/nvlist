package nl.weeaboo.vn.impl.test.integration;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.impl.image.PixelTextureData;
import nl.weeaboo.vn.impl.image.VolatileTextureData;
import nl.weeaboo.vn.impl.image.WritableScreenshot;
import nl.weeaboo.vn.impl.render.GLScreenRenderer;
import nl.weeaboo.vn.impl.render.RenderStats;

public class VolatileScreenshotTest extends IntegrationTest {

    @Test
    public void volatileScreenshot() {
        IEnvironment env = novel.getEnv();
        Rect glRect = env.getRenderEnv().getGLClip();

        GLScreenRenderer renderer = new GLScreenRenderer(env.getRenderEnv(), new RenderStats());

        // Volatile screenshot
        VolatileTextureData vp = (VolatileTextureData)screenshot(renderer, true, glRect).getPixels();
        TextureRegion vregion = vp.toTextureRegion();
        Assert.assertNotNull(vregion);

        // Non-Volatile screenshot
        PixelTextureData np = (PixelTextureData)screenshot(renderer, false, glRect).getPixels();
        TextureRegion nregion = np.toTextureRegion();
        Assert.assertNotNull(nregion);

        // Check that volatile and non-volatile screenshots are identical
        Assert.assertEquals(nregion.getRegionWidth(), vregion.getRegionWidth());
        Assert.assertEquals(nregion.getRegionHeight(), vregion.getRegionHeight());
    }

    private WritableScreenshot screenshot(GLScreenRenderer renderer, boolean isVolatile, Rect glRect) {
        WritableScreenshot ss = new WritableScreenshot(Short.MIN_VALUE, isVolatile);
        renderer.renderScreenshot(ss, glRect);
        return ss;
    }

}
