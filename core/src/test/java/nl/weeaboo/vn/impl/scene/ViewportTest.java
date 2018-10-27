package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.impl.input.MockInput;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.IVisualElement;

public final class ViewportTest {

    private static final double EPSILON = 0.001;

    private MockInput input = new MockInput();
    private Viewport viewport;

    @Before
    public void before() {
        viewport = new Viewport();
        viewport.setBounds(0, 0, 10, 10);
    }

    @Test
    public void testScroll() {
        IImageDrawable image = image(100, 50);
        image.setPos(-10, -20);
        viewport.add(image);

        // The scroll bounds limit the scroll x/y to the bounds of the viewport's contents
        assertScrollBounds(-10, -20, 100 - viewport.getWidth(), 50 - viewport.getHeight());

        // Test that the scroll limits are actually applied
        viewport.scroll(-999_999, -999_999);
        assertScrollPos(-10, -20);

        viewport.scroll(999_999, 999_999);
        assertScrollPos(80, 20); // Max x: image.x + image.w - viewport.w
    }

    /**
     * Using the {@link Viewport#setContents(IVisualElement)} method, you can replace all child components in one call.
     */
    @Test
    public void testSetContents() {
        IImageDrawable a = image(10, 10);
        IImageDrawable b = image(20, 20);

        viewport.add(a);
        assertContents(a);

        // Replace the contents with 'b', this removes 'a'
        viewport.setContents(b);
        assertContents(b);
    }

    @Test
    public void testInputHandling() {
        viewport.add(image(100, 50));
        assertScrollPos(0, 0);

        // Drag the mouse, then check that the viewport scrolls by that amount (limited by the scroll bounds)
        input.mousePress();
        handleInput();
        input.pointerMoved(-50, 0);
        handleInput();
        input.pointerMoved(0, -999);
        handleInput();

        assertScrollPos(50, 40);

        // When you release the mouse, the viewport stops scrolling with mouse movement
        input.mouseRelease();
        handleInput();
        input.pointerMoved(10, 10);
        handleInput();

        assertScrollPos(50, 40);
    }

    private void handleInput() {
        Matrix parentTransform = Matrix.identityMatrix();
        viewport.handleInput(parentTransform, input);
    }

    private void assertContents(IVisualElement... expected) {
        Assert.assertEquals(ImmutableSet.copyOf(expected),
                ImmutableSet.copyOf(viewport.getChildren()));
    }

    private void assertScrollPos(double x, double y) {
        Assert.assertEquals(x, viewport.getScrollX(), EPSILON);
        Assert.assertEquals(y, viewport.getScrollY(), EPSILON);
    }

    private void assertScrollBounds(double x, double y, double w, double h) {
        RectAssert.assertEquals(Area2D.of(x, y, w, h), viewport.getScrollBounds(), EPSILON);
    }

    private static IImageDrawable image(int w, int h) {
        return CoreTestUtil.newImage(w, h);
    }

}
