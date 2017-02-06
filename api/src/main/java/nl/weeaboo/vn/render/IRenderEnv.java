package nl.weeaboo.vn.render;

import java.io.Serializable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;

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
    public double getScale();

    /**
     * @return The bounds of the full OpenGL rendering bounds in virtual
     *         coordinates, ignoring any offset or clipping.
     */
    public Rect2D getGLScreenVirtualBounds();

    /**
     * @return {@code true} when running on a touchscreen device.
     */
    public boolean isTouchScreen();

}
