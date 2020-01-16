package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.core.IEnvironment;

public interface IBackBuffer extends Disposable {

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

}
