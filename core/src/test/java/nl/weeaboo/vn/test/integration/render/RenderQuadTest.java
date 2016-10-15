package nl.weeaboo.vn.test.integration.render;

import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.DrawTransform;

public class RenderQuadTest extends RenderIntegrationTest {

    @Test
    public void render() {
        DrawBuffer drawBuffer = getDrawBuffer();

        DrawTransform dt = new DrawTransform();
        ITexture tex = getTexture("test");
        renderer.startLayer();
        drawBuffer.drawQuad(dt, 0xFFFFFFFF, tex, Area2D.of(0, 0, 1280, 720), ITexture.DEFAULT_UV);

        renderer.render();

        checkRenderResult("quad");
    }

}
