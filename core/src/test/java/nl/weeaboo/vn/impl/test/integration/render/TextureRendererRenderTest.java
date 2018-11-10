package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.image.TextureRenderer;
import nl.weeaboo.vn.impl.scene.ImageDrawable;

public class TextureRendererRenderTest extends RenderIntegrationTest {

    private TextureRenderer textureRenderer;
    private ImageDrawable drawable;

    @Before
    public void before() {
        textureRenderer = new TextureRenderer(getTexture("a"));

        drawable = new ImageDrawable();
        drawable.setRenderer(textureRenderer);
        drawable.setSize(1280, 720);
    }

    @After
    public void after() {
        drawable.destroy();
    }

    @Test
    public void renderBasic() {
        doRender();
        checkRenderResult("texture-basic");
    }

    @Test
    public void renderUvOffset() {
        textureRenderer.setUV(.25, .25, .5, .5);
        doRender();
        checkRenderResult("texture-uv-offset");
    }

    private void doRender() {
        drawable.draw(getDrawBuffer());
        render();
    }

}
