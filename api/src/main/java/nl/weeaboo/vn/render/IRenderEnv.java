package nl.weeaboo.vn.render;

import java.io.Serializable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;

/**
 * Exposes properties of the main render target (screen size, etc.)
 */
public interface IRenderEnv extends Serializable {

    /**
     * @return The OpenGL clipping rectangle for the virtual screen.
     */
    Rect getGLClip();

    /**
     * @return The virtual width of the screen.
     */
    int getWidth();

    /**
     * @return The virtual height of the screen.
     */
    int getHeight();

    /**
     * @return The virtual size of the screen.
     */
    Dim getVirtualSize();

    /**
     * @return The render area in physical screen pixels.
     */
    Rect getRealClip();

    /**
     * @return The dimensions of the physical window in screen pixels.
     */
    Dim getScreenSize();

    /**
     * @return The scale factor from virtual coords to real coords.
     */
    double getScale();

    /**
     * @return The bounds of the full OpenGL rendering bounds in virtual
     *         coordinates, ignoring any offset or clipping.
     */
    Rect2D getGLScreenVirtualBounds();

}
