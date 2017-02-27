package nl.weeaboo.vn.scene;

import nl.weeaboo.common.Rect2D;

public interface IAxisAlignedContainer extends IVisualGroup, IPositionable {

    @Override
    IAxisAlignedContainer getParent();

    /**
     * Checks if the specified X/Y point lies 'inside' this container's bounds.
     *
     * @param cx The X-coordinate of the point to test.
     * @param cy The Y-coordinate of the point to test.
     */
    boolean contains(double cx, double cy);

    /**
     * @return The top-left X-coordinate of the container relative to its parent.
     */
    double getX();

    /**
     * @return The top-left Y-coordinate of the container relative to its parent.
     */
    double getY();

    /**
     * @return The width of the container.
     */
    double getWidth();

    /**
     * @return The height of the container.
     */
    double getHeight();

    /**
     * The bounds of the container.
     */
    Rect2D getBounds();

    /**
     * Sets the visibility flag.
     */
    void setVisible(boolean v);

}
