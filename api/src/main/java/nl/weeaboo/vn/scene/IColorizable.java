package nl.weeaboo.vn.scene;

public interface IColorizable {

    /**
     * @return The current color (red, green, blue) and alpha, packed together into a single int in ARGB order.
     */
    int getColorARGB();

    /**
     * @return The current color (red, green, blue), packed into an int in RGB order. The value of the highest 8 bits is
     *         undefined.
     */
    int getColorRGB();

    /** Returns the red component of the current color in the range {@code [0.0, 1.0]} */
    double getRed();

    /** Returns the green component of the current color in the range {@code [0.0, 1.0]} */
    double getGreen();

    /** Returns the blue component of the current color in the range {@code [0.0, 1.0]} */
    double getBlue();

    /** Returns the alpha component of the current color in the range {@code [0.0, 1.0]} */
    double getAlpha();

    /**
     * Sets the alpha component of the current color.
     *
     * @param a The new alpha value in the range {@code [0.0, 1.0]}.
     */
    void setAlpha(double a); // Calls setColor(r, g, b, a)

    /**
     * Sets the RGB components of the current color.
     *
     * @param rgb RGB color packed into an XXRRGGBB integer.
     */
    void setColorRGB(int rgb); //Calls setColor(r, g, b, a)

    /**
     * Sets the current color and alpha.
     *
     * @param argb ARGB color+alpha packed into an AARRGGBB integer.
     */
    void setColorARGB(int argb); //Calls setColor(r, g, b, a)

    /**
     * Sets the RGB components of the current color, where each component is in the range {@code [0.0, 1.0]}.
     */
    void setColor(double r, double g, double b); //Calls setColor(r, g, b, a)

    /**
     * Sets the the current color and alpha, where each component is in the range {@code [0.0, 1.0]}.
     */
    void setColor(double r, double g, double b, double a);

}
