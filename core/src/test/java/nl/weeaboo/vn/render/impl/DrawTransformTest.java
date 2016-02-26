package nl.weeaboo.vn.render.impl;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;

public class DrawTransformTest {

    @Test
    public void defaultValues() {
        DrawTransform dt = new DrawTransform();
        Assert.assertEquals(0, dt.getZ());
        Assert.assertEquals(true, dt.isClipEnabled());
        Assert.assertEquals(BlendMode.DEFAULT, dt.getBlendMode());
        Assert.assertEquals(Matrix.identityMatrix(), dt.getTransform());
    }

    @Test
    public void copyConstructor() {
        DrawTransform dt = new DrawTransform();
        dt.setZ((short)1);
        dt.setClipEnabled(false);
        dt.setBlendMode(BlendMode.ADD);
        dt.setTransform(Matrix.translationMatrix(1, 2));

        dt = new DrawTransform(dt);
        Assert.assertEquals(1, dt.getZ());
        Assert.assertEquals(false, dt.isClipEnabled());
        Assert.assertEquals(BlendMode.ADD, dt.getBlendMode());
        Assert.assertEquals(Matrix.translationMatrix(1, 2), dt.getTransform());
    }

}
