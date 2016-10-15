package nl.weeaboo.vn.test.integration.render;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.render.impl.DrawTransform;

public class RenderQuadTest extends RenderIntegrationTest {

    private ITexture tex;

    @Before
    public void before() {
        tex = getTexture("test");
    }

    @Test
    public void render() {
        renderer.startLayer();
        renderer.drawQuad(tex, Area2D.of(0, 0, 1280, 720));
        renderer.render();

        checkRenderResult("quad");
    }

    @Test
    public void renderRotated() {
        renderer.startLayer();

        DrawTransform dt = new DrawTransform();
        MutableMatrix mm = new MutableMatrix();
        mm.translate(400, 0);
        mm.rotate(128); // Rotate right around (0,0) by a quarter turn
        dt.setTransform(mm.immutableCopy());
        renderer.drawQuad(tex, dt, Area2D.of(0, 0, 640, 400));
        renderer.render();

        checkRenderResult("quad-rotated");
    }

}
