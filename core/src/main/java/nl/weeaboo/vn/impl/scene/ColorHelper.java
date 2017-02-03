package nl.weeaboo.vn.impl.scene;

import java.io.IOException;
import java.io.ObjectInputStream;

import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.impl.core.TransientListenerSupport;
import nl.weeaboo.vn.render.RenderUtil;

@CustomSerializable
final class ColorHelper extends TransientListenerSupport {

    private static final long serialVersionUID = 1L;

    private final double[] rgba = { 1.0, 1.0, 1.0, 1.0 };

    private transient int colorARGBInt;

    public ColorHelper() {
        initTransients();
    }

    private void initTransients() {
        colorARGBInt = RenderUtil.packRGBAtoARGB(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    public int getColorARGB() {
        return colorARGBInt;
    }

    public final double getRed() {
        return rgba[0];
    }

    public final double getGreen() {
        return rgba[1];
    }

    public final double getBlue() {
        return rgba[2];
    }

    public final double getAlpha() {
        return rgba[3];
    }

    public final void setAlpha(double a) {
        setColor(rgba[0], rgba[1], rgba[2], a);
    }

    public final void setColorRGB(int rgb) {
        int ri = (rgb >> 16) & 0xFF;
        int gi = (rgb >> 8) & 0xFF;
        int bi = (rgb) & 0xFF;

        setColor(ri / 255.0, gi / 255.0, bi / 255.0);
    }

    public final void setColorARGB(int argb) {
        int ai = (argb >> 24) & 0xFF;
        int ri = (argb >> 16) & 0xFF;
        int gi = (argb >> 8) & 0xFF;
        int bi = (argb) & 0xFF;

        setColor(ri / 255.0, gi / 255.0, bi / 255.0, ai / 255.0);
    }

    public final void setColor(double r, double g, double b) {
        setColor(r, g, b, rgba[3]);
    }

    public void setColor(double r, double g, double b, double a) {
        if (rgba[0] != r || rgba[1] != g || rgba[2] != b || rgba[3] != a) {
            rgba[0] = r;
            rgba[1] = g;
            rgba[2] = b;
            rgba[3] = a;

            colorARGBInt = RenderUtil.packRGBAtoARGB(rgba[0], rgba[1], rgba[2], rgba[3]);
        }
    }

}
