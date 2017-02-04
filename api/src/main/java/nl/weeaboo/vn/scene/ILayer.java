package nl.weeaboo.vn.scene;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.IScreenshotBuffer;

public interface ILayer extends IVisualGroup, IPositionable {

    @Override
    ILayer getParent();

    /**
     * Adds a drawable to the layer.
     */
    void add(IDrawable d);

    /**
     * @return {@code true} if the specified layer is a descendant of this layer.
     */
    boolean containsLayer(ILayer layer);

    /**
     * @return A read-only view of all layer children (non-recursive).
     */
    Iterable<? extends ILayer> getSubLayers();

    /**
     * @return A buffer for pending screenshots. Screenshots requests queued in this buffer will be fullfilled
     *         at some later time.
     */
    IScreenshotBuffer getScreenshotBuffer();

    /**
     * @return The top-left X-coordinate of the layer relative to its parent layer.
     */
    double getX();

    /**
     * @return The top-left Y-coordinate of the layer relative to its parent layer.
     */
    double getY();

    /**
     * @return The width of the layer.
     */
    double getWidth();

    /**
     * @return The height of the layer.
     */
    double getHeight();

    /**
     * The bounds of the layer.
     */
    Rect2D getBounds();

    /**
     * Changes the relative render order of this element.
     * @see IVisualElement#getZ()
     */
    void setZ(short z);

    /**
     * Sets the visibility flag.
     */
    void setVisible(boolean v);

}
