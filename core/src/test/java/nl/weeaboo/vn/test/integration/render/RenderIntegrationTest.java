package nl.weeaboo.vn.test.integration.render;

import org.junit.After;
import org.junit.Before;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.gdx.test.pixmap.ScreenshotHelper;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.RenderTestHelper;
import nl.weeaboo.vn.test.integration.IntegrationTest;

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

    public DrawBuffer getDrawBuffer() {
        return renderer.getDrawBuffer();
    }

    protected ITexture getTexture(String path) {
        ITexture texture = env.getImageModule().getTexture(FilePath.of(path));
        // Set filtering to nearest so we don't get trolled by slight interpolation differences on the build server
        GdxTextureUtil.getTexture(texture).setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }

    public void checkRenderResult(String testName) {
        Rect glClip = env.getRenderEnv().getGLClip();
        checkRenderResult(testName, glClip.x, glClip.y, glClip.w, glClip.h);
    }
    public void checkRenderResult(String testName, int x, int y, int w, int h) {
        Pixmap actual = ScreenshotHelper.screenshot(x, y, w, h);

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
