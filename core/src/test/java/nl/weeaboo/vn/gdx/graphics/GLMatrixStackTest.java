package nl.weeaboo.vn.gdx.graphics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.DisposeUtil;

public final class GLMatrixStackTest {

    private SpriteBatch batch;
    private GLMatrixStack stack;

    private Matrix4 original;

    @Before
    public void before() {
        HeadlessGdx.init();
        batch = new SpriteBatch();
        stack = new GLMatrixStack(batch);

        original = stack.getCombined();
    }

    @After
    public void after() {
        DisposeUtil.dispose(batch);
    }

    /**
     * Every push puts a new mutable matrix on the stack. Calling pop afterwards removes that matrix again.
     */
    @Test
    public void pushPop() {
        for (int n = 0; n < 10; n++) {
            stack.pushMatrix();
            stack.translate(1, 2);
            stack.popMatrix();

            Assert.assertEquals(original, stack.getCombined());
        }
    }

    @Test
    public void testScale() {
        // Scale, then invert the scale to get back to the original transform
        stack.scale(2.0, 4.0);
        stack.scale(0.5, 0.25);

        Assert.assertEquals(original, stack.getCombined());
    }

    @Test
    public void testTranslate() {
        // Translate, then invert the translation to get back to the original transform
        stack.translate(1.0, 2.0);
        stack.translate(-1.0, -2.0);

        Assert.assertEquals(original, stack.getCombined());
    }

    @Test
    public void testMultiply() {
        Matrix4 m = new Matrix4();
        m.translate(1, 2, 3);

        // Multiply with a matric, then invert the multiplication to get back to the original transform
        stack.multiply(m);
        m.inv();
        stack.multiply(m);
    }

}
