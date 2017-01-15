package nl.weeaboo.vn.math;

import nl.weeaboo.common.FastMath;

public final class MutableMatrix extends AbstractMatrix {

    private static final long serialVersionUID = 1L;

    public MutableMatrix() {
        this(1, 0, 0, 0, 1, 0);
    }
    public MutableMatrix(double m00, double m01, double m02, double m10, double m11, double m12) {
        super(m00, m01, m02, m10, m11, m12);
    }
    public MutableMatrix(AbstractMatrix m) {
        super(m);
    }

    //Functions
    public Matrix immutableCopy() {
        return new Matrix(m00, m01, m02, m10, m11, m12);
    }

    public void add(AbstractMatrix m) {
        m00 += m.m00; m01 += m.m01; m02 += m.m02;
        m10 += m.m10; m11 += m.m11; m12 += m.m12;
    }

    public void sub(AbstractMatrix m) {
        m00 -= m.m00; m01 -= m.m01; m02 -= m.m02;
        m10 -= m.m10; m11 -= m.m11; m12 -= m.m12;
    }

    public void mul(double s) {
        m00 *= s; m01 *= s; m02 *= s;
        m10 *= s; m11 *= s; m12 *= s;
    }

    public void mul(AbstractMatrix m) {
        double r00 = m00 * m.m00 + m01 * m.m10;
        double r01 = m00 * m.m01 + m01 * m.m11;
        double r02 = m00 * m.m02 + m01 * m.m12 + m02;

        double r10 = m10 * m.m00 + m11 * m.m10;
        double r11 = m10 * m.m01 + m11 * m.m11;
        double r12 = m10 * m.m02 + m11 * m.m12 + m12;

        m00 = r00; m01 = r01; m02 = r02;
        m10 = r10; m11 = r11; m12 = r12;
    }

    public void translate(double x, double y) {
        m02 += x * m00 + y * m01;
        m12 += x * m10 + y * m11;
    }

    public void rotate(double angle) {
        double cos = FastMath.fastCos((float)angle);
        double sin = FastMath.fastSin((float)angle);

        double a, b;

        a =  cos * m00 + sin * m01;
        b = -sin * m00 + cos * m01;
        m00 = a;
        m01 = b;

        a =  cos * m10 + sin * m11;
        b = -sin * m10 + cos * m11;
        m10 = a;
        m11 = b;
    }

    public void scale(double sx, double sy) {
        m00 *= sx;
        m10 *= sx;
        m11 *= sy;
        m01 *= sy;
    }

    //Getters
    public static MutableMatrix identityMatrix() {
        return new MutableMatrix();
    }
    public static MutableMatrix translationMatrix(double x, double y) {
        return new MutableMatrix(1, 0, x, 0, 1, y);
    }
    public static MutableMatrix scaleMatrix(double x, double y) {
        return new MutableMatrix(x, 0, 0, 0, y, 0);
    }
    public static MutableMatrix rotationMatrix(double angle) {
        double cos = FastMath.fastCos((float)angle);
        double sin = FastMath.fastSin((float)angle);

        return new MutableMatrix(cos, -sin, 0, sin, cos, 0);
    }

}
