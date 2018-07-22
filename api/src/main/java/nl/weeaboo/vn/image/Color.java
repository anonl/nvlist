package nl.weeaboo.vn.image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.google.errorprone.annotations.Immutable;

import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.render.RenderUtil;

@Immutable
@CustomSerializable
public final class Color implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Color WHITE = new Color(1, 1, 1, 1);

    private final double r;
    private final double g;
    private final double b;
    private final double a;

    private transient int argbInt;

    private Color(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        initTransients();
    }

    /**
     * Creates a color from a packed 24-bit integer in RGB format with the alpha value set to fully opaque.
     */
    public static Color fromRGB(int rgb) {
        return fromARGB(rgb | 0xFF000000);
    }

    /**
     * Creates a color from three separate R/G/B values in the range {@code [0.0, 1.0]}.
     */
    public static Color fromRGB(double r, double g, double b) {
        return fromRGBA(r, g, b, 1.0);
    }

    /**
     * Creates a color from a packed 32-bit integer in ARGB format.
     */
    public static Color fromARGB(int argb) {
        int ai = (argb >> 24) & 0xFF;
        int ri = (argb >> 16) & 0xFF;
        int gi = (argb >> 8) & 0xFF;
        int bi = (argb) & 0xFF;

        return fromRGBA(ri / 255.0, gi / 255.0, bi / 255.0, ai / 255.0);
    }

    /**
     * Creates a color from four separate R/G/B/A values in the range {@code [0.0, 1.0]}.
     */
    public static Color fromRGBA(double r, double g, double b, double a) {
        return new Color(r, g, b, a);
    }

    private void initTransients() {
        argbInt = RenderUtil.packRGBAtoARGB(r, g, b, a);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    /**
     * Returns the color as a packed 32-bit integer in ARGB order.
     */
    public int getARGB() {
        return argbInt;
    }

    /**
     * Returns the color as a packed 24-bit integer in RGB order.
     */
    public int getRGB() {
        return argbInt & 0xFFFFFF;
    }

    /** Returns the red component of the color in the range {@code [0.0, 1.0]} */
    public double getRed() {
        return r;
    }

    /** Returns the green component of the color in the range {@code [0.0, 1.0]} */
    public double getGreen() {
        return g;
    }

    /** Returns the blue component of the color in the range {@code [0.0, 1.0]} */
    public double getBlue() {
        return b;
    }

    /** Returns the alpha component of the color in the range {@code [0.0, 1.0]} */
    public double getAlpha() {
        return a;
    }

}
