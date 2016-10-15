package nl.weeaboo.vn.test.integration.render;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;

public class RenderTriangleGridTest extends RenderIntegrationTest {

    private ITexture tex;

    @Before
    public void before() {
        tex = getTexture("test");
    }

    @Test
    public void render() {
        renderer.startLayer();
        renderer.drawTriangleGrid(tex, Area2D.of(0, 0, 1280, 720));
        renderer.render();

        checkRenderResult("trianglegrid");
    }

}
