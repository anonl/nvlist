package nl.weeaboo.vn.impl.render;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;

public class GLScreenRendererTest {

    static {
        HeadlessGdx.init();
    }

    private RenderTestHelper renderer;

    @Before
    public void before() {
        renderer = new RenderTestHelper(CoreTestUtil.BASIC_ENV);
    }

    @After
    public void after() {
        renderer.destroy();
    }

    /** Trivial test to see if nothing crashes trying to render an empty draw buffer */
    @Test
    public void renderNothing() {
        renderer.render();
    }

    /** Trivial test to see if nothing crashes trying to render an empty draw buffer */
    @Test
    public void renderCustom() {
        DrawBuffer drawBuffer = renderer.getDrawBuffer();

        final AtomicBoolean renderCalled = new AtomicBoolean();
        DrawTransform dt = new DrawTransform();
        drawBuffer.drawCustom(dt, 0xFFFFFFFF, new IRenderLogic() {
            @Override
            public void render(IScreenRenderer<?> renderer) {
                renderCalled.set(true);
            }
        });

        renderer.render();
        Assert.assertEquals(true, renderCalled.get());
    }

}
