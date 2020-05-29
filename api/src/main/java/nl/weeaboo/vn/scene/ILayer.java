package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.image.IScreenshotBuffer;

/**
 * Group of visual elements.
 */
public interface ILayer extends IAxisAlignedContainer {

    @Override
    ILayer getParent();

    /**
     * Adds contents to the layer.
     */
    void add(IVisualElement d);

    /**
     * Returns {@code true} if the specified layer is a descendant of this layer.
     */
    boolean containsLayer(ILayer layer);

    /**
     * Returns a read-only view of all layer children (non-recursive).
     */
    Iterable<? extends ILayer> getSubLayers();

    /**
     * Returns a buffer for pending screenshots. Screenshots requests queued in this buffer will be fullfilled
     *         at some later time.
     */
    IScreenshotBuffer getScreenshotBuffer();

    /**
     * Changes the relative render order of this element.
     * @see IVisualElement#getZ()
     */
    void setZ(short z);

}
