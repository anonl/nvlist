package nl.weeaboo.vn.impl.test.integration.render;

import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Before;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.gdx.test.pixmap.ScreenshotHelper;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.DrawBuffer;
import nl.weeaboo.vn.impl.render.RenderTestHelper;
import nl.weeaboo.vn.impl.test.integration.IntegrationTest;

public abstract class RenderIntegrationTest extends IntegrationTest {

    // Allow a small difference in color to account for rounding errors
    private static final int MAX_COLOR_DIFF = 2;

    protected boolean generate = false;

    protected RenderTestHelper renderer;
    private PixmapEquality pixmapEquals;

    @Before
    public final void beforeRenderTest() {
        env.updateRenderEnv(Rect.of(0, 60, 640, 360), Dim.of(640, 480));
        renderer = new RenderTestHelper(env.getRenderEnv());

        pixmapEquals = new PixmapEquality();
        pixmapEquals.setMaxColorDiff(MAX_COLOR_DIFF);
    }

    @After
    public final void afterRenderTest() {
        renderer.destroy();
    }

    /**
     * @return The draw buffer used by the renderer.
     */
    public DrawBuffer getDrawBuffer() {
        return renderer.getDrawBuffer();
    }

    protected @Nullable ITexture getTexture(String path) {
        ITexture texture = env.getImageModule().getTexture(FilePath.of(path));
        // Set filtering to nearest so we don't get trolled by slight interpolation differences on the build server
        Texture gdxTexture = GdxTextureUtil.getTexture(texture);
        if (gdxTexture != null) {
            gdxTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        }
        return texture;
    }

    /**
     * Checks the render result by comparing a screenshot of the current screen to an image file containing the expected
     * result.
     *
     * @see #checkRenderResult(String, Rect)
     */
    public void checkRenderResult(String testName) {
        checkRenderResult(testName, env.getRenderEnv().getGLClip());
    }

    /**
     * Checks the render result by comparing a screenshot of a sub-rect of the current screen to an image file
     * containing the expected result.
     *
     * @param glRect Clip rectangle in OpenGL viewport coordinates (OpenGL uses a flipped y-axis, so y=0 is the bottom
     *        pixel).
     */
    public void checkRenderResult(String testName, Rect glRect) {
        Pixmap actual = ScreenshotHelper.screenshot(glRect.x, glRect.y, glRect.w, glRect.h);

        String outputPath = "src/test/resources/render/" + testName + ".png";
        FileHandle fileHandle = Gdx.files.local(outputPath);
        if (generate) {
            PixmapIO.writePNG(fileHandle, actual);
        } else {
            Pixmap expected = new Pixmap(fileHandle);
            pixmapEquals.assertEquals(expected, actual);
        }
    }

}
