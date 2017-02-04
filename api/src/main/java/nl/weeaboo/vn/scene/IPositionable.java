package nl.weeaboo.vn.scene;

public interface IPositionable {

    /**
     * Sets the X-coordinate.
     * @see #setPos(double, double)
     */
    void setX(double x);

    /**
     * Sets the Y-coordinate.
     * @see #setPos(double, double)
     */
    void setY(double y);

    /**
     * Stretches to the given width.
     * @see #setSize(double, double)
     */
    void setWidth(double w);

    /**
     * Stretches to the given height.
     * @see #setSize(double, double)
     */
    void setHeight(double h);

    /**
     * Modified the position by the specified amount: {@code setPos(x + dx, y + dy)}.
     * @see #setPos(double, double)
     */
    void translate(double dx, double dy);

    /**
     * Sets the X/Y-coordinates at the same time.
     */
    void setPos(double x, double y);

    /**
     * Changes the width/height at the same time.
     * @see #setWidth(double)
     * @see #setHeight(double)
     */
    void setSize(double w, double h);

    /**
     * Moves and stretches this drawable to make it fit inside the specified bounding box.
     */
    void setBounds(double x, double y, double w, double h);

}
