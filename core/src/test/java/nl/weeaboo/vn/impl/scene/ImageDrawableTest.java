package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.TestTexture;
import nl.weeaboo.vn.impl.image.TextureStub;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.math.Matrix;

public class ImageDrawableTest {

    private static final double E = CoreTestUtil.EPSILON;

    private final ImageDrawable image = new ImageDrawable();

    @Test
    public void transform() {
        int x = -50;
        int y = -50;
        int w = 100;
        int h = 100;

        image.setTexture(new TestTexture(w, h));

        // Bounds
        image.setBounds(x, y, w, h);
        assertVisualBounds(x, y, w, h);

        image.setPos(100, 100);
        assertVisualBounds(100, 100, w, h);
        image.setSize(200, 200);
        assertVisualBounds(100, 100, 200, 200);

        image.setBounds(1, 2, 3, 4);
        assertVisualBounds(1, 2, 3, 4);

        image.translate(1, 2);
        assertVisualBounds(2, 4, 3, 4);

        image.setX(x);
        image.setY(y);
        image.setWidth(w);
        image.setHeight(h);
        assertVisualBounds(x, y, w, h);

        // Rotated bounds
        image.setRotation(64); // Rotate 1/8th circle clockwise around top-left
        final double diagonal = Math.sqrt(w * w + h * h);
        assertVisualBounds(x - diagonal / 2, y, diagonal, diagonal);

        image.rotate(64);
        assertVisualBounds(x - h, y, w, h);

        // Scaled
        image.setRotation(0);

        image.setScale(2);
        assertVisualBounds(x, y, w * 2, h * 2);

        image.setScale(0.5, 2);
        assertVisualBounds(x, y, w * .5, h * 2);

        image.scale(2);
        assertVisualBounds(x, y, w, h * 4);

        image.scale(2, -1);
        assertVisualBounds(x, y - h * 4, w * 2, h * 4);

        // Align
        image.setPos(0, 0);
        image.setScale(1, 1);
        image.setAlign(0.5, 0.5);
        assertVisualBounds(x, y, w, h);
    }

    @Test
    public void drawableColor() {
        // Getters/setters using doubles
        double alpha = 0.35;

        image.setAlpha(alpha);
        Assert.assertEquals(alpha, image.getAlpha(), E);

        image.setColor(.2, .4, .6);
        Assert.assertEquals(.2, image.getRed(), E);
        Assert.assertEquals(.4, image.getGreen(), E);
        Assert.assertEquals(.6, image.getBlue(), E);
        Assert.assertEquals(alpha, image.getAlpha(), E);

        image.setColor(.2, .4, .6, .8);
        Assert.assertEquals(.2, image.getRed(), E);
        Assert.assertEquals(.4, image.getGreen(), E);
        Assert.assertEquals(.6, image.getBlue(), E);
        Assert.assertEquals(.8, image.getAlpha(), E);

        // Getters/setters using ints
        image.setAlpha(alpha);
        image.setColorRGB(0xA0806040);
        Assert.assertEquals(alpha, image.getAlpha(), E);
        Assert.assertEquals(0x806040, image.getColorRGB());

        image.setColorARGB(0x20406080);
        Assert.assertEquals(0x406080, image.getColorRGB());
        Assert.assertEquals(0x20406080, image.getColorARGB());
    }

    @Test
    public void drawableAttributes() {
        // Z
        Assert.assertEquals(0, image.getZ());
        image.setZ(Short.MAX_VALUE);
        Assert.assertEquals(Short.MAX_VALUE, image.getZ());
        image.setZ(Short.MIN_VALUE);
        Assert.assertEquals(Short.MIN_VALUE, image.getZ());

        // Visible
        image.setAlpha(0.2);
        Assert.assertTrue(image.isVisible());
        Assert.assertTrue(image.isVisible(0.2));
        Assert.assertFalse(image.isVisible(0.3));
        image.setVisible(false);
        Assert.assertFalse(image.isVisible());

        // Clipping
        Assert.assertTrue(image.isClipEnabled());
        image.setClipEnabled(false);
        Assert.assertFalse(image.isClipEnabled());

        // Blend mode
        Assert.assertEquals(BlendMode.DEFAULT, image.getBlendMode());
        image.setBlendMode(BlendMode.ADD);
        Assert.assertEquals(BlendMode.ADD, image.getBlendMode());
    }

    @Test
    public void imageAttributes() {
        final ITexture alpha = new TextureStub(50, 50);
        final ITexture beta = new TextureStub(100, 100);

        image.setTexture(beta);
        Assert.assertEquals(0, image.getX(), E);
        Assert.assertEquals(0, image.getY(), E);

        image.setTexture(alpha, Direction.CENTER);
        assertVisualBounds(25, 25, 50, 50);
    }

    /**
     * Hit-test using the collision shape.
     */
    @Test
    public void testCollision() {
        int x = 50;
        int y = 50;
        int w = 100;
        int h = 100;
        image.setBounds(x, y, w, h);

        assertContains(49, 75, false);
        assertContains(151, 75, false);
        assertContains(75, 49, false);
        assertContains(75, 151, false);

        assertContains(75, 75, true);

        // Rotate 1/8th turn around the top-left coordinate
        image.rotate(64);
        final double diagonal = Math.sqrt(w * w + h * h);
        assertContains(x, y + 1, true);
        assertContains(x, y + diagonal - 1, true);
    }

    @Test
    public void testBaseTransform() {
        Matrix matrix = Matrix.rotationMatrix(64);
        image.setBaseTransform( matrix);

        Assert.assertEquals(matrix, image.getBaseTransform());
        Assert.assertEquals(matrix, image.getTransform());

        image.rotate(64);

        Assert.assertEquals(matrix, image.getBaseTransform());
        Assert.assertNotEquals(matrix, image.getTransform());
    }

    @Test
    public void testScaleToFit() {
        image.setTexture(new TestTexture(10, 5));

        image.scaleToFit(20, 20);
        // scaleToFit scales uniformly to the largest size that fits entirely within the given bounds
        assertVisualBounds(0, 0, 20, 10);
    }

    private void assertContains(double x, double y, boolean expected) {
        Assert.assertEquals(expected, image.contains(x, y));
    }

    private void assertVisualBounds(double x, double y, double w, double h) {
        RectAssert.assertEquals(Rect2D.of(x, y, w, h), image.getVisualBounds(), E);
    }

}
