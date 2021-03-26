package nl.weeaboo.vn.impl.image;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.EventListenerStub;

public final class TextureRendererTest {

    private static final double EPSILON = 1e-3;

    private final EventListenerStub eventListener = new EventListenerStub();
    private TextureRenderer renderer;

    @Before
    public void before() {
        HeadlessGdx.init();

        renderer = new TextureRenderer(new TextureMock(100, 100));
        renderer.onAttached(eventListener);
    }

    @Test
    public void testUv() {
        assertUv(0, 0, 1, 1);

        renderer.setUV(.1, .2);
        assertUv(0, 0, .1, .2);
        eventListener.consumeEventCount(1);

        renderer.setUV(.1, .2, .3, .4);
        assertUv(.1, .2, .3, .4);
        eventListener.consumeEventCount(1);

        // Double-setting the same UV doesn't fire a change event
        renderer.setUV(.1, .2, .3, .4);
        eventListener.consumeEventCount(0);

        renderer.scrollUV(.2, .1);
        assertUv(.3, .3, .3, .4);
        eventListener.consumeEventCount(1);

        // Negative width/height is also allowed
        renderer.setUV(1, 1, -1, -2);
        assertUv(1, 1, -1, -2);
        eventListener.consumeEventCount(1);
    }

    @Test
    public void testChangeTexture() {
        TextureMock newTexture = new TextureMock(50, 50);

        renderer.setTexture(newTexture);
        RectAssert.assertEquals(Rect2D.of(0, 0, 50, 50), renderer.getVisualBounds(), EPSILON);
        // Two change events are triggered: size changed, texture changed
        eventListener.consumeEventCount(2);

        // Setting the same texture doesn't fire a change event
        renderer.setTexture(newTexture);
        eventListener.consumeEventCount(0);
    }

    @Test
    public void testNullTexture() {
        renderer.setTexture(null);
        Assert.assertNull(renderer.getTexture());
        Assert.assertEquals(0.0, renderer.getNativeWidth(), EPSILON);
        Assert.assertEquals(0.0, renderer.getNativeHeight(), EPSILON);

        // The default constructor also sets a null texture
        renderer = new TextureRenderer();
        Assert.assertNull(renderer.getTexture());
    }

    private void assertUv(double u, double v, double w, double h) {
        RectAssert.assertEquals(Area2D.of(u, v, w, h), renderer.getUV(), EPSILON);
    }

}
