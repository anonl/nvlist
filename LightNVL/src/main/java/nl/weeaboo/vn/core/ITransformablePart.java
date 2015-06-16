package nl.weeaboo.vn.core;

import nl.weeaboo.vn.math.Matrix;

public interface ITransformablePart extends IDrawablePart {

	/**
	 * Returns the base width of the transformable, before any transforms are
	 * applied.
	 */
	public double getUnscaledWidth();

	/**
	 * Returns the base height of the transformable, before any transforms are
	 * applied.
	 */
	public double getUnscaledHeight();

	/**
	 * @see #getTransform()
	 */
	public Matrix getBaseTransform();

	/**
	 * The transform is obtained by modifying the base transform with this
	 * drawable's X/Y position, scale, rotation. The image align isn't included
	 * in the transform.
	 *
	 * @return The final transformation matrix used when rendering this
	 *         drawable.
	 * @see #getBaseTransform()
	 */
	public Matrix getTransform();

	/**
	 * @return The current rotation between <code>0</code> and <code>512</code>.
	 */
	public double getRotation();

	/**
	 * @return The horizontal scaling factor.
	 * @see #setScale(double, double)
	 */
	public double getScaleX();

	/**
	 * @return The vertical scaling factor.
	 * @see #setScale(double, double)
	 */
	public double getScaleY();

	/**
	 * @see #setAlign(double, double)
	 */
	public double getAlignX();

	/**
	 * @see #setAlign(double, double)
	 */
	public double getAlignY();

	/**
	 * @return {@link #getAlignX()} multiplied by the current width.
	 */
	public double getAlignOffsetX();

	/**
	 * @return {@link #getAlignY()} multiplied by the current height.
	 */
	public double getAlignOffsetY();

	/**
	 * @see #getTransform()
	 */
	public void setBaseTransform(Matrix transform);

	/**
	 * @param rot The new rotation between <code>0</code> and <code>512</code>.
	 */
	public void setRotation(double rot);

	/**
	 * Sets the horizontal and vertical scale factors to the same value.
	 *
	 * @see #setScale(double, double)
	 */
	public void setScale(double s);

	/**
	 * Sets the horizontal and vertical scaling factors for this transformable.
	 */
	public void setScale(double sx, double sy);

	/**
	 * Changes the display position of this transformable relative to its X/Y
	 * coordinates. An x-align value of <code>0.0</code> uses the left,
	 * <code>0.5</code> uses the center and <code>1.0</code> is right-aligned.
	 *
	 * @param xFrac Relative display position from the X-coordinate.
	 * @param yFrac Relative display position from the Y-coordinate.
	 */
	public void setAlign(double xFrac, double yFrac);

}
