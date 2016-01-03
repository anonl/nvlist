package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.math.Matrix;

public interface ITransformable extends IDrawable {

	/**
	 * @see #getTransform()
	 */
    Matrix getBaseTransform();

	/**
	 * The transform is obtained by modifying the base transform with this
	 * drawable's X/Y position, scale, rotation. The image align isn't included
	 * in the transform.
	 *
	 * @return The final transformation matrix used when rendering this
	 *         drawable.
	 * @see #getBaseTransform()
	 */
    @Override
    Matrix getTransform();

	/**
	 * @return The current rotation between <code>0</code> and <code>512</code>.
	 */
    double getRotation();

	/**
	 * @return The horizontal scaling factor.
	 * @see #setScale(double, double)
	 */
    double getScaleX();

	/**
	 * @return The vertical scaling factor.
	 * @see #setScale(double, double)
	 */
    double getScaleY();

	/**
	 * @see #setAlign(double, double)
	 */
    double getAlignX();

	/**
	 * @see #setAlign(double, double)
	 */
    double getAlignY();

	/**
	 * @return {@link #getAlignX()} multiplied by the current width.
	 */
    double getAlignOffsetX();

	/**
	 * @return {@link #getAlignY()} multiplied by the current height.
	 */
    double getAlignOffsetY();

	/**
	 * @see #getTransform()
	 */
    void setBaseTransform(Matrix transform);

    /**
     * Adjusts the current rotation.
     *
     * @see #setRotation(double)
     */
    void rotate(double r);

	/**
	 * @param rot The new rotation between <code>0</code> and <code>512</code>.
	 */
    void setRotation(double rot);

    /**
     * Adjusts the current scale.
     *
     * @see #scale(double, double)
     * @see #setScale(double)
     */
    void scale(double s);

    /**
     * Adjusts the current scale.
     *
     * @see #setScale(double, double)
     */
    void scale(double sx, double sy);

	/**
	 * Sets the horizontal and vertical scale factors to the same value.
	 *
	 * @see #setScale(double, double)
	 */
    void setScale(double s);

	/**
	 * Sets the horizontal and vertical scaling factors for this transformable.
	 */
    void setScale(double sx, double sy);

	/**
	 * Changes the display position of this transformable relative to its X/Y
	 * coordinates. An x-align value of <code>0.0</code> uses the left,
	 * <code>0.5</code> uses the center and <code>1.0</code> is right-aligned.
	 *
	 * @param xFrac Relative display position from the X-coordinate.
	 * @param yFrac Relative display position from the Y-coordinate.
	 */
    void setAlign(double xFrac, double yFrac);

}
