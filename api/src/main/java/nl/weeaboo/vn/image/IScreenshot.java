package nl.weeaboo.vn.image;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.render.IAsyncRenderTask;
import nl.weeaboo.vn.scene.IVisualElement;

/**
 * Wrapper object for screenshot operations. Since screenshots can only be taken during rendering, it may take
 * several frames for a scheduled screenshot to become available.
 */
public interface IScreenshot extends IAsyncRenderTask {

    /**
     * Marks this screenshot object as transient; its pixels won't be serialized.
     */
    void markTransient();

    /**
     * @deprecated Deprecated since NVList 4.0
     */
    @Deprecated
    void makeTransient();

    /**
     * A volatile screenshot only stores its pixels on the GPU. As a consequence, it may lose its pixels at
     * any time.
     */
    boolean isVolatile();

    /**
     * Returns whether this screenshot is transient.
     *
     * @see #markTransient()
     */
    @Override
    boolean isTransient();

    /**
     * @return The screen size in pixels when this screenshot was taken.
     */
    Dim getScreenSize();

    /**
     * The Z-index at which this screenshot will be, or was, taken.
     *
     * @see IVisualElement#getZ()
     */
    short getZ();

    /**
     * Warning: May return {@code null} if the screenshot is not yet available or volatile.
     */
    ITextureData getPixels();

}
