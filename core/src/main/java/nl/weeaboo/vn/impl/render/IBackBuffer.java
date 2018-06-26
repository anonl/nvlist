package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.core.IEnvironment;

public interface IBackBuffer {

    /**
     * Disposes the native resources held by this object. No further methods may be called on the object
     * afterwards.
     */
    void dispose();

    /**
     * Set up the rendering state for rendering to this back buffer.
     *
     * @see #setWindowSize(IEnvironment, Dim)
     * @see #end()
     */
    SpriteBatch begin();

    /**
     * Finishes the rendering started with a call to {@link #begin()}.
     *
     * @see #begin()
     */
    void end();

    /**
     * Renders this back buffer to the screen (may be a no-op for some implementations).
     */
    void flip();

    /**
     * Notifies the back buffer of a change to the dimensions of the native window.
     */
    void setWindowSize(IEnvironment env, Dim windowSize);

    /**
     * Returns the viewport for rendering to the back buffer.
     * @see #getScene2dViewport()
     */
    Viewport getScreenViewport();

    /**
     * Returns the viewport for Scene2d.
     * @see #getScreenViewport()
     */
    Viewport getScene2dViewport();

}
