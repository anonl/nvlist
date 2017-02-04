package nl.weeaboo.vn.math;

import static nl.weeaboo.test.SerializeTester.deserializeObject;
import static nl.weeaboo.test.SerializeTester.serializeObject;
import static nl.weeaboo.vn.ApiTestUtil.EPSILON;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

import nl.weeaboo.vn.ApiTestUtil;

public class MatrixTest {

    private static final float GL_EPSILON = 0.0001f;

    @Test
    public void equalsTest() {
        Matrix alpha = new Matrix(0, 0, 0, 0, 0, 0);
        Matrix beta = new Matrix(0, 0, 0, 0, -0.01, 0);

        new EqualsTester()
            .addEqualityGroup(alpha, alpha.mutableCopy())
            .addEqualityGroup(beta)
            .testEquals();

        Assert.assertTrue(alpha.equals(alpha, 0));
        Assert.assertFalse(alpha.equals(beta, 0.001));
        Assert.assertTrue(alpha.equals(beta, 0.01));
    }

    @Test
    public void matrixGetters() {
        Matrix alpha = new Matrix(11, 22, 33, 44, 55, 66);

        Assert.assertEquals(11, alpha.getScaleX(), EPSILON);
        Assert.assertEquals(22, alpha.getShearX(), EPSILON);
        Assert.assertEquals(33, alpha.getTranslationX(), EPSILON);
        Assert.assertEquals(44, alpha.getShearY(), EPSILON);
        Assert.assertEquals(55, alpha.getScaleY(), EPSILON);
        Assert.assertEquals(66, alpha.getTranslationY(), EPSILON);

        Matrix identity = Matrix.identityMatrix();
        Assert.assertFalse(identity.hasShear());
        Assert.assertTrue(alpha.hasShear());
        Assert.assertFalse(identity.hasScale());
        Assert.assertTrue(alpha.hasScale());
        Assert.assertFalse(identity.hasTranslation());
        Assert.assertTrue(alpha.hasTranslation());
    }

    @Test
    public void matrixIdentityTest() {
        Matrix identity = Matrix.identityMatrix();

        // No-op transforms re-use the identity matrix object
        Assert.assertSame(identity, Matrix.translationMatrix(0, 0));
        Assert.assertSame(identity, Matrix.rotationMatrix(0));
        Assert.assertSame(identity, Matrix.scaleMatrix(1, 1));

        // Constructor functions and copy functions should return identical results
        testMatrixCopyConstructor(0, 0);
        testMatrixCopyConstructor(1, 0);
        testMatrixCopyConstructor(0, 1);
        testMatrixCopyConstructor(1, 1);

        Random random = new Random();
        double scale = 1024; // SIN_LUT.length * 2
        for (int n = 0; n < 1000; n++) {
            double x = scale * random.nextDouble() - 0.5 * scale;
            double y = scale * random.nextDouble() - 0.5 * scale;
            testMatrixCopyConstructor(x, y);
        }
    }

    private static void testMatrixCopyConstructor(double x, double y) {
        Matrix identity = Matrix.identityMatrix();
        MathTestUtil.assertEquals(Matrix.translationMatrix(x, y), identity.translatedCopy(x, y), 0);
        MathTestUtil.assertEquals(Matrix.rotationMatrix(x), identity.rotatedCopy(x), 0);
        MathTestUtil.assertEquals(Matrix.scaleMatrix(x, y), identity.scaledCopy(x, y), 0);
    }

    @Test
    public void matrixElementWiseOpsTest() {
        Matrix alpha = new Matrix(1, 2, 3, 4, 5, 6);

        // Multiply
        MathTestUtil.assertEquals(new Matrix(-1, -2, -3, -4, -5, -6), alpha.multiply(-1));

        // Plus
        MathTestUtil.assertEquals(new Matrix(2, 4, 6, 8, 10, 12), alpha.plus(alpha));

        // Minus
        MathTestUtil.assertEquals(new Matrix(0, 0, 0, 0, 0, 0), alpha.minus(alpha));

        // Plus and minus negate each other
        MathTestUtil.assertEquals(alpha, alpha.plus(alpha).minus(alpha));
        MathTestUtil.assertEquals(alpha, alpha.minus(alpha).plus(alpha));
    }

    @Test
    public void matrixMultiply() {
        Matrix base = new Matrix(33, 22, 11, 44, 55, 66);

        // Constructor functions and copy functions should return identical results
        testMatrixMultiply(base, 0, 0);
        testMatrixMultiply(base, 1, 0);
        testMatrixMultiply(base, 0, 1);
        testMatrixMultiply(base, 1, 1);

        Random random = new Random();
        double scale = 1024; // SIN_LUT.length * 2
        for (int n = 0; n < 1000; n++) {
            double x = scale * random.nextDouble() - 0.5 * scale;
            double y = scale * random.nextDouble() - 0.5 * scale;
            testMatrixMultiply(base, x, y);
        }
    }

    private static void testMatrixMultiply(Matrix base, double x, double y) {
        Matrix multiplied = base.multiply(Matrix.translationMatrix(x, y));
        base = base.translatedCopy(x, y);
        MathTestUtil.assertEquals(base, multiplied);

        multiplied = base.multiply(Matrix.rotationMatrix(x));
        base = base.rotatedCopy(x);
        MathTestUtil.assertEquals(base, multiplied);

        multiplied = base.multiply(Matrix.scaleMatrix(x, y));
        base = base.scaledCopy(x, y);
        MathTestUtil.assertEquals(base, multiplied);
    }

    @Test
    public void transform() {
        Matrix transform = new Matrix(33, 22, 11, 44, 55, 66);

        Random random = new Random();
        for (int n = 0; n < 1000; n++) {
            float x = random.nextFloat();
            float y = random.nextFloat();

            Vec2 expected = new Vec2(33 * x + 22 * y + 11, 44 * x + 55 * y + 66);
            ApiTestUtil.assertEquals(expected.x, expected.y, transform.transform(x, y), EPSILON);

            Vec2 v = new Vec2(x, y);
            transform.transform(v);
            ApiTestUtil.assertEquals(expected.x, expected.y, v, EPSILON);

            float[] pts = {x, y};
            transform.transform(pts, 0, 1);
            ApiTestUtil.assertEquals(expected.x, expected.y, new Vec2(pts[0], pts[1]), EPSILON);
        }
    }

    @Test
    public void mutableMatrix() {
        MutableMatrix matrix = new MutableMatrix();
        MutableMatrix copy = new MutableMatrix(matrix);
        Assert.assertEquals(matrix, copy);
        Assert.assertEquals(matrix, MutableMatrix.identityMatrix());

        Matrix base = new Matrix(33, 22, 11, 44, 55, 66);

        testMutableEquivalent(base, 0, 0);
        testMutableEquivalent(base, 1, 0);
        testMutableEquivalent(base, 0, 1);
        testMutableEquivalent(base, 1, 1);

        Random random = new Random();
        double scale = 1024; // SIN_LUT.length * 2
        for (int n = 0; n < 1000; n++) {
            double x = scale * random.nextDouble() - 0.5 * scale;
            double y = scale * random.nextDouble() - 0.5 * scale;
            testMutableEquivalent(base, x, y);
        }
    }

    private static void testMutableEquivalent(Matrix base, double x, double y) {
        // Translate, rotate, scale
        MutableMatrix mutable = base.mutableCopy();
        mutable.translate(x, y);
        MathTestUtil.assertEquals(base.translatedCopy(x, y), mutable);
        MathTestUtil.assertEquals(Matrix.translationMatrix(x, y), MutableMatrix.translationMatrix(x, y));

        mutable = base.mutableCopy();
        mutable.rotate(x);
        MathTestUtil.assertEquals(base.rotatedCopy(x), mutable, 0);
        MathTestUtil.assertEquals(Matrix.rotationMatrix(x), MutableMatrix.rotationMatrix(x));

        mutable = base.mutableCopy();
        mutable.scale(x, y);
        MathTestUtil.assertEquals(base.scaledCopy(x, y), mutable, 0);
        MathTestUtil.assertEquals(Matrix.scaleMatrix(x, y), MutableMatrix.scaleMatrix(x, y));

        // Add, sub, multiply scalar
        Matrix alpha = new Matrix(3, 2, 1, 6, 5, 4);

        mutable = base.mutableCopy();
        mutable.add(alpha);
        MathTestUtil.assertEquals(base.plus(alpha), mutable);
        mutable.sub(alpha);
        MathTestUtil.assertEquals(base, mutable);
        mutable.mul(x);
        MathTestUtil.assertEquals(base.multiply(x), mutable);

        // Multiply
        mutable = base.mutableCopy();
        mutable.mul(Matrix.translationMatrix(x, y));
        base = base.translatedCopy(x, y);
        MathTestUtil.assertEquals(base, mutable);

        mutable = base.mutableCopy();
        mutable.mul(Matrix.rotationMatrix(x));
        base = base.rotatedCopy(x);
        MathTestUtil.assertEquals(base, mutable);

        mutable = base.mutableCopy();
        mutable.mul(Matrix.scaleMatrix(x, y));
        base = base.scaledCopy(x, y);
        MathTestUtil.assertEquals(base, mutable);
    }

    @Test
    public void glMatrixTest() {
        Matrix alpha = new Matrix(11, 22, 33, 44, 55, 66);
        float[] glMatrix = alpha.toGLMatrix();

        // GL matrices are 4x4 and column-major
        Assert.assertArrayEquals(new float[] {
                11, 44, 0, 0,
                22, 55, 0, 0,
                0,  0,  1, 0,
                33, 66, 0, 1
        }, glMatrix, GL_EPSILON);

        // Generated GL matrices are cached (implementation detail, but important to know if that ever changes)
        Assert.assertSame(glMatrix, alpha.toGLMatrix());
    }

    @Test
    public void serialize() {
        Matrix alpha = new Matrix(11, 22, 33, 44, 55, 66);

        Assert.assertEquals(alpha, deserializeObject(serializeObject(alpha), Matrix.class));
        Assert.assertEquals(alpha, deserializeObject(serializeObject(alpha.mutableCopy()), MutableMatrix.class));
    }

}
