package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.gdx.test.pixmap.ScreenshotHelper;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.graphics.PixmapTester;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.DrawBuffer;
import nl.weeaboo.vn.impl.render.RenderTestHelper;
import nl.weeaboo.vn.impl.render.RenderTestHelper.ISpriteBatchConsumer;
import nl.weeaboo.vn.impl.test.integration.IntegrationTest;
import nl.weeaboo.vn.render.IDrawTransform;

@Category(GdxUiTest.class)
public abstract class RenderIntegrationTest extends IntegrationTest {

    // Allow a small difference in color to account for rounding errors
    private static final int MAX_COLOR_DIFF = 2;

    private RenderTestHelper renderer;
    private PixmapTester pixmapTester;

    @Before
    public final void beforeRenderTest() {
        env.updateRenderEnv(Rect.of(0, 60, 640, 360), Dim.of(640, 480));
        renderer = new RenderTestHelper(env.getRenderEnv());

        pixmapTester = new PixmapTester();
        pixmapTester.setMaxColorDiff(MAX_COLOR_DIFF);
    }

    @After
    public final void afterRenderTest() {
        pixmapTester.dispose();
        renderer.destroy();
    }

    /**
     * @return The draw buffer used by the renderer.
     */
    public DrawBuffer getDrawBuffer() {
        return renderer.getDrawBuffer();
    }

    protected ITexture getTexture(String path) {
        ITexture texture = env.getImageModule().getTexture(FilePath.of(path));
        Assert.assertNotNull(texture);
        // Set filtering to nearest so we don't get trolled by slight interpolation differences on the build server
        setFilterNearest(texture);
        return texture;
    }

    protected static void setFilterNearest(ITexture texture) {
        Texture gdxTexture = GdxTextureUtil.getTexture(texture);
        if (gdxTexture != null) {
            gdxTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        }
    }

    protected void render() {
        launcher.render();
        renderer.render();
    }


    protected void drawQuad(ITexture tex, Area2D bounds) {
        renderer.drawQuad(tex, bounds);
    }

    protected void drawQuad(ITexture tex, IDrawTransform transform, Area2D bounds) {
        renderer.drawQuad(tex, transform, bounds);
    }

    protected void drawText(double dx, double dy, ITextLayout textLayout) {
        renderer.drawText(dx, dy, textLayout);
    }

    protected void drawText(IDrawTransform transform, double dx, double dy, ITextLayout textLayout) {
        renderer.drawText(transform, dx, dy, textLayout);
    }

    protected void drawTriangleGrid(ITexture tex, Area2D bounds) {
        renderer.drawTriangleGrid(tex, bounds);
    }

    protected void renderCustom(ISpriteBatchConsumer renderOp) {
        renderer.renderCustom(renderOp);
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

        pixmapTester.checkRenderResult(testName, actual);
    }

}
