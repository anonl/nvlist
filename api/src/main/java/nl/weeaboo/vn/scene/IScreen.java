package nl.weeaboo.vn.scene;

import java.io.Serializable;

import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.render.IOffscreenRenderTaskBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.render.IRenderEnvConsumer;

public interface IScreen extends Serializable, IUpdateable, IRenderEnvConsumer {

    /**
     * Creates a new layer and adds it to {@code parentLayer}.
     *
     * @throws IllegalArgumentException If {@code parentLayer} isn't attached to this screen.
     */
    ILayer createLayer(ILayer parentLayer);

    /**
     * @return The root layer of this screen.
     */
    ILayer getRootLayer();

    /**
     * @return The current default layer for new drawables that are added to this screen.
     */
    ILayer getActiveLayer();

    /**
     * @see #getActiveLayer()
     */
    void setActiveLayer(ILayer layer);

    /**
     * @return Information about the current text box.
     */
    IScreenTextState getTextState();

    /**
     * @return An object for starting and monitoring offscreen render tasks.
     */
    IOffscreenRenderTaskBuffer getOffscreenRenderTaskBuffer();

    /**
     * Returns information about the rendering environment.
     */
    IRenderEnv getRenderEnv();

}
