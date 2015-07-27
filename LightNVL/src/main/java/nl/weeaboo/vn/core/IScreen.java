package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.entity.Entity;

public interface IScreen extends Serializable, IUpdateable {

    /**
     * Creates a new entity and adds it to this screen.
     */
    Entity createEntity();

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
	 * Returns information about the rendering environment.
	 */
    IRenderEnv getRenderEnv();

    /**
     * Sets the rendering environment which contains information about the
     * clipping/scaling performed by OpenGL.
     */
    void setRenderEnv(IRenderEnv env);

}
