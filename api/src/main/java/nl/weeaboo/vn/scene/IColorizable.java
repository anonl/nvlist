package nl.weeaboo.vn.scene;

public interface IColorizable {

    /**
     * @return The current color (red, green, blue) and alpha, packed together into a single int in ARGB
     *         order.
     */
    int getColorARGB();

    /**
     * @return The current color (red, green, blue), packed into an int in RGB order. The value of the highest
     *         8 bits is undefined.
     */
    int getColorRGB();

    double getRed();
    double getGreen();
    double getBlue();
    double getAlpha();

    void setAlpha(double a); // Calls setColor(r, g, b, a)
    void setColorRGB(int rgb); //Calls setColor(r, g, b, a)
    void setColorARGB(int argb); //Calls setColor(r, g, b, a)
    void setColor(double r, double g, double b); //Calls setColor(r, g, b, a)
    void setColor(double r, double g, double b, double a);

}
