package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.entity.Entity;

public interface IScreen extends Serializable, IUpdateable {

    /**
     * Creates a new entity and adds it to this screen.
     */
    public Entity createEntity();

	/**
	 * Creates a new layer and adds it to {@code parentLayer}.
	 *
	 * @throws IllegalArgumentException If {@code parentLayer} isn't attached to this screen.
	 */
	public ILayer createLayer(ILayer parentLayer);

	/**
	 * @return The root layer of this screen.
	 */
	public ILayer getRootLayer();

	/**
	 * @return The current default layer for new drawables that are added to this screen.
	 */
	public ILayer getActiveLayer();

	/**
	 * Returns information about the rendering environment.
	 */
	public IRenderEnv getRenderEnv();

    /**
     * Sets the rendering environment which contains information about the
     * clipping/scaling performed by OpenGL.
     */
    public void setRenderEnv(IRenderEnv env);

}
