package nl.weeaboo.vn.render.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.scene.impl.Layer;

public class GLScreenRendererTest {

    static {
        HeadlessGdx.init();
    }

    private GLScreenRenderer renderer;
    private DrawBuffer drawBuffer;

    @Before
    public void before() {
        renderer = new GLScreenRenderer(CoreTestUtil.BASIC_ENV, new RenderStats());
        drawBuffer = new DrawBuffer();
    }

    @After
    public void after() {
        renderer.destroy();
    }

    /** Trivial test to see if nothing crashes trying to render an empty draw buffer */
    @Test
    public void renderNothing() {
        renderer.render(drawBuffer);
    }

    /** Trivial test to see if nothing crashes trying to render an empty draw buffer */
    @Test
    public void renderCustom() {
        startLayer();
        final AtomicBoolean renderCalled = new AtomicBoolean();
        DrawTransform dt = new DrawTransform();
        drawBuffer.drawCustom(dt, 0xFFFFFFFF, new IRenderLogic() {
            @Override
            public void render(IScreenRenderer<?> renderer) {
                renderCalled.set(true);
            }
        });

        renderer.render(drawBuffer);
        Assert.assertEquals(true, renderCalled.get());
    }

    private void startLayer() {
        int layerId = drawBuffer.reserveLayerIds(1);
        drawBuffer.startLayer(layerId, new Layer(null));
    }

}
