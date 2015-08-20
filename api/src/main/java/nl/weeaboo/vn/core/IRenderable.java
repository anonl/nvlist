package nl.weeaboo.vn.core;

import nl.weeaboo.common.Rect2D;

public interface IRenderable {

	public boolean consumeChanged();

	public double getX();
	public double getY();
	public short getZ();
	public double getWidth();
	public double getHeight();

	public boolean isVisible();

	/**
	 * @return The axis-aligned bounding box for this renderable.
	 */
	public Rect2D getBounds();

	/**
	 * @return The current rendering environment, or <code>null</code> if no rendering environment has been
	 *         set yet.
	 * @see #setRenderEnv(IRenderEnv)
	 */
	public IRenderEnv getRenderEnv();

	/**
	 * Sets the rendering environment which contains information about the
	 * clipping/scaling performed by OpenGL.
	 */
	public void setRenderEnv(IRenderEnv env);

}
