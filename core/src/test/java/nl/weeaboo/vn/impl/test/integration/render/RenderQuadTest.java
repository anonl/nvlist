package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.TextureStub;
import nl.weeaboo.vn.impl.render.DrawTransform;
import nl.weeaboo.vn.math.MutableMatrix;

public class RenderQuadTest extends RenderIntegrationTest {

    private ITexture tex;

    @Before
    public void before() {
        tex = getTexture("test");
    }

    @Test
    public void render() {
        renderer.drawQuad(tex, Area2D.of(0, 0, 1280, 720));
        renderer.render();

        checkRenderResult("quad");
    }

    @Test
    public void renderInvalidTexture() {
        // The GL renderer only knows how to render TextureAdapters, not TextureStubs
        TextureStub invalidTex = new TextureStub(1, 1);

        renderer.drawQuad(invalidTex, Area2D.of(0, 0, 1280, 720));
        renderer.render();

        checkRenderResult("quad-invalidtexture");
    }

    @Test
    public void renderRotated() {
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
