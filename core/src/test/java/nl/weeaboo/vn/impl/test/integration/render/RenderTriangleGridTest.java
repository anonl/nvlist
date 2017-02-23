package nl.weeaboo.vn.impl.test.integration.render;

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
        // Render triangle grid on the left side
        renderer.drawTriangleGrid(tex, Area2D.of(0, 0, 640, 720));
        // Render on the right side, mirrored in both axes
        renderer.drawTriangleGrid(tex, Area2D.of(1280, 720, -640, -720));
        renderer.render();

        checkRenderResult("trianglegrid");
    }

}
