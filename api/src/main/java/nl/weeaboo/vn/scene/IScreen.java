package nl.weeaboo.vn.scene;

import java.io.Serializable;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.render.IOffscreenRenderTaskBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.render.IRenderEnvConsumer;

/**
 * Top-level container for the visual elements in a {@link IContext}.
 */
public interface IScreen extends Serializable, IUpdateable, IRenderEnvConsumer {

    /**
     * Creates a new layer and adds it to {@code parentLayer}.
     *
     * @throws IllegalArgumentException If {@code parentLayer} isn't attached to this screen.
     */
    ILayer createLayer(ILayer parentLayer);

    /**
     * Returns the root layer of this screen.
     */
    ILayer getRootLayer();

    /**
     * Returns the current default layer for new drawables that are added to this screen.
     */
    ILayer getActiveLayer();

    /**
     * @see #getActiveLayer()
     */
    void setActiveLayer(ILayer layer);

    /**
     * Returns information about the current text box.
     */
    IScreenTextState getTextState();

    /**
     * Returns an object for starting and monitoring offscreen render tasks.
     */
    IOffscreenRenderTaskBuffer getOffscreenRenderTaskBuffer();

    /**
     * Returns information about the rendering environment.
     */
    IRenderEnv getRenderEnv();

}
