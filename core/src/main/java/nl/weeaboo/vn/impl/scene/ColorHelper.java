package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.image.Color;
import nl.weeaboo.vn.impl.core.TransientListenerSupport;

@CustomSerializable
final class ColorHelper extends TransientListenerSupport {

    private static final long serialVersionUID = 1L;

    private Color color = Color.WHITE;

    public int getColorARGB() {
        return color.getARGB();
    }

    public final double getRed() {
        return color.getRed();
    }

    public final double getGreen() {
        return color.getGreen();
    }

    public final double getBlue() {
        return color.getBlue();
    }

    public final double getAlpha() {
        return color.getAlpha();
    }

    public final void setAlpha(double a) {
        setColor(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    public final void setColorRGB(int rgb) {
        Color color = Color.fromARGB(rgb);
        setColor(color.getRed(), color.getGreen(), color.getBlue(), getAlpha());
    }

    public final void setColorARGB(int argb) {
        setColor(Color.fromARGB(argb));
    }

    public final void setColor(double r, double g, double b) {
        setColor(r, g, b, color.getAlpha());
    }

    public void setColor(double r, double g, double b, double a) {
        setColor(Color.fromRGBA(r, g, b, a));
    }

    public void setColor(Color newColor) {
        if (!newColor.equals(color)) {
            color = newColor;

            fireListeners();
        }
    }

}
