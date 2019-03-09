package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.image.ITexture;

@Category(GdxUiTest.class)
public class RenderTriangleGridTest extends RenderIntegrationTest {

    private ITexture tex;

    @Before
    public void before() {
        tex = Checks.checkNotNull(getTexture("test"));
    }

    @Test
    public void testRender() {
        // Render triangle grid on the left side
        drawTriangleGrid(tex, Area2D.of(0, 0, 640, 720));
        // Render on the right side, mirrored in both axes
        drawTriangleGrid(tex, Area2D.of(1280, 720, -640, -720));
        render();

        checkRenderResult("trianglegrid");
    }

}
