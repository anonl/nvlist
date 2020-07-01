package nl.weeaboo.vn.impl.test.integration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.common.Rect;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.PixelTextureData;
import nl.weeaboo.vn.impl.image.VolatileTextureData;
import nl.weeaboo.vn.impl.image.WritableScreenshot;
import nl.weeaboo.vn.impl.render.GLScreenRenderer;
import nl.weeaboo.vn.impl.render.RenderStats;

@Category(GdxUiTest.class)
public class VolatileScreenshotTest extends IntegrationTest {

    @Test
    public void volatileScreenshot() {
        IEnvironment env = novel.getEnv();
        Rect glRect = env.getRenderEnv().getGLClip();

        GLScreenRenderer renderer = new GLScreenRenderer(env.getRenderEnv(), new RenderStats());

        // Volatile screenshot
        VolatileTextureData vp = (VolatileTextureData)screenshot(renderer, true, glRect).getPixels();
        ITexture vregion = vp.toTexture(1, 1);
        Assert.assertNotNull(vp.toTexture(1, 1));

        // Non-Volatile screenshot
        PixelTextureData np = (PixelTextureData)screenshot(renderer, false, glRect).getPixels();
        ITexture nregion = np.toTexture(1, 1);
        Assert.assertNotNull(nregion);

        // Check that volatile and non-volatile screenshots are identical
        Assert.assertEquals(nregion.getWidth(), vregion.getWidth(), 0.0);
        Assert.assertEquals(nregion.getHeight(), vregion.getHeight(), 0.0);
    }

    private WritableScreenshot screenshot(GLScreenRenderer renderer, boolean isVolatile, Rect glRect) {
        WritableScreenshot ss = new WritableScreenshot(Short.MIN_VALUE, isVolatile);
        renderer.renderScreenshot(ss, glRect);
        return ss;
    }

}
