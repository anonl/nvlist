package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.DirectBackBuffer;
import nl.weeaboo.vn.impl.render.GdxViewports;
import nl.weeaboo.vn.impl.render.HybridBackBuffer;
import nl.weeaboo.vn.impl.render.IBackBuffer;

@Category(GdxUiTest.class)
public class BackBufferRenderTest extends RenderIntegrationTest {

    private static final Rect RESULT_GL_RECT = Rect.of(0, 0, 640, 480);

    private ITexture tex;
    private HybridBackBuffer hybrid;
    private DirectBackBuffer direct;

    @Before
    public void before() {
        tex = getTexture("a");

        GdxViewports viewports = new GdxViewports(Dim.of(1280, 720));
        hybrid = new HybridBackBuffer(Dim.of(1280, 720), viewports);
        direct = new DirectBackBuffer(viewports);
    }

    @Test
    public void testDirectBackBuffer() {
        renderWith(direct);

        checkRenderResult("backbuffer/direct", RESULT_GL_RECT);
    }

    @Test
    public void testHybridBackBuffer() {
        renderWith(hybrid);

        checkRenderResult("backbuffer/hybrid", RESULT_GL_RECT);
    }

    private void renderWith(IBackBuffer backBuffer) {
        backBuffer.begin();
        try {
            backBuffer.setWindowSize(getEnv(), Dim.of(640, 480));
            drawQuad(tex, Area2D.of(0, 0, 1280, 720));
            render();
        } finally {
            backBuffer.end();
        }
    }

}
