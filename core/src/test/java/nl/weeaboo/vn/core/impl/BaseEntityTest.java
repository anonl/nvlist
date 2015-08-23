package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.LvnTestUtil;
import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.ITransformablePart;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.image.impl.TextureStub;

public class BaseEntityTest extends AbstractEntityTest {

    private static final double E = LvnTestUtil.EPSILON;

	@Test
	public void transformablePartTest() {
        Entity e = NvlTestUtil.newImage(pr, scene);
        ITransformablePart transformable = e.getPart(pr.transformable);

		double x = -50;
		double y = -50;
		double w = 100;
		double h = 100;

		// Bounds
		transformable.setBounds(x, y, w, h);
        LvnTestUtil.assertEquals(x, y, w, h, transformable.getBounds());

        transformable.setPos(100, 100);
        LvnTestUtil.assertEquals(100, 100, w, h, transformable.getBounds());
        transformable.setSize(200, 200);
        LvnTestUtil.assertEquals(100, 100, 200, 200, transformable.getBounds());

        transformable.setBounds(1, 2, 3, 4);
        LvnTestUtil.assertEquals(1, 2, 3, 4, transformable.getBounds());

        transformable.setX(x);
        transformable.setY(y);
        transformable.setWidth(w);
        transformable.setHeight(h);
        LvnTestUtil.assertEquals(x, y, w, h, transformable.getBounds());

		// Rotated bounds
		transformable.setRotation(64); // Rotate 1/8th circle clockwise around top-left
		final double diagonal = Math.sqrt(w*w + h*h);
        LvnTestUtil.assertEquals(x - diagonal / 2, y, diagonal, diagonal, transformable.getBounds());

		// Scaled
		transformable.setRotation(0);
		transformable.setScale(0.5, 2);
        LvnTestUtil.assertEquals(x, y, w * .5, h * 2, transformable.getBounds());

		// Align
		transformable.setPos(0, 0);
		transformable.setScale(1, 1);
		transformable.setAlign(0.5, 0.5);
        LvnTestUtil.assertEquals(x, y, w, h, transformable.getBounds());
	}

	@Test
	public void drawablePartColor() {
        Entity e = NvlTestUtil.newImage(pr, scene);
        IDrawablePart drawable = e.getPart(pr.drawable);

        // Getters/setters using doubles
        double alpha = 0.35;

        drawable.setAlpha(alpha);
        Assert.assertEquals(alpha, drawable.getAlpha(), E);

        drawable.setColor(.2, .4, .6);
        Assert.assertEquals(.2, drawable.getRed(), E);
        Assert.assertEquals(.4, drawable.getGreen(), E);
        Assert.assertEquals(.6, drawable.getBlue(), E);
        Assert.assertEquals(alpha, drawable.getAlpha(), E);

        // Getters/setters using ints
        drawable.setColorRGB(0xA0806040);
        Assert.assertEquals(alpha, drawable.getAlpha(), E);
        Assert.assertEquals(0x806040, drawable.getColorRGB());

        drawable.setColorARGB(0x20406080);
        Assert.assertEquals(0x406080, drawable.getColorRGB());
        Assert.assertEquals(0x20406080, drawable.getColorARGB());
	}

    @Test
    public void drawablePartAttributes() {
        DrawablePart drawable = new DrawablePart();

        // Z
        Assert.assertEquals(0, drawable.getZ());
        drawable.setZ(Short.MAX_VALUE);
        Assert.assertEquals(Short.MAX_VALUE, drawable.getZ());
        drawable.setZ(Short.MIN_VALUE);
        Assert.assertEquals(Short.MIN_VALUE, drawable.getZ());

        // Visible
        drawable.setAlpha(0.2);
        Assert.assertTrue(drawable.isVisible());
        Assert.assertTrue(drawable.isVisible(0.2));
        Assert.assertFalse(drawable.isVisible(0.3));
        drawable.setVisible(false);
        Assert.assertFalse(drawable.isVisible());

        // Clipping
        Assert.assertTrue(drawable.isClipEnabled());
        drawable.setClipEnabled(false);
        Assert.assertFalse(drawable.isClipEnabled());

        // Blend mode
        Assert.assertEquals(BlendMode.DEFAULT, drawable.getBlendMode());
        drawable.setBlendMode(BlendMode.ADD);
        Assert.assertEquals(BlendMode.ADD, drawable.getBlendMode());

        // Render env
        Assert.assertEquals(null, drawable.getRenderEnv());
        drawable.setRenderEnv(NvlTestUtil.BASIC_ENV);
        Assert.assertEquals(NvlTestUtil.BASIC_ENV, drawable.getRenderEnv());
    }

    @Test
    public void imagePart() {
        TransformablePart transformable = new TransformablePart();
        ImagePart imagePart = new ImagePart(transformable);

        ITexture alpha = new TextureStub(50, 50);
        ITexture beta = new TextureStub(100, 100);

        imagePart.setTexture(beta);
        Assert.assertEquals(0, transformable.getX(), E);
        Assert.assertEquals(0, transformable.getY(), E);

        imagePart.setTexture(alpha, 5);
        LvnTestUtil.assertEquals(25, 25, 50, 50, transformable.getBounds());
    }

}
