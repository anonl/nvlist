package nl.weeaboo.vn.scene;

import javax.annotation.Nullable;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawTransform;

public interface IDrawable extends IVisualElement, IColorizable, IDrawTransform, IPositionable {

    /**
     * @return The parent layer that contains this drawable, or {@code null} if not attached to any layer.
     */
    @Nullable ILayer getLayer();

    @Override
    BlendMode getBlendMode();

    @Override
    boolean isClipEnabled();

    /**
     * A utility method to check if {@code visible && getAlpha() >= minAlpha}.
     *
     * @param minAlpha The minimum alpha to consider 'visible'.
     * @return {@code true} if this drawable is visible and at least matches the minimum alpha.
     */
    boolean isVisible(double minAlpha);

    /**
     * Checks if the specified X/Y point lies 'inside' this renderable. What's considered inside may be
     * different depending on the type of renderable.
     *
     * @param cx The X-coordinate of the point to test.
     * @param cy The Y-coordinate of the point to test.
     * @return <code>true</code> if the point is contained within this renderable.
     */
    boolean contains(double cx, double cy);

    /**
     * X-coordinate of the drawable. How this corresponds to the final visual bounds depends on the type of drawable.
     */
    double getX();

    /**
     * Y-coordinate of the drawable. How this corresponds to the final visual bounds depends on the type of drawable.
     */
    double getY();

    /**
     * Width of the drawable.
     * @see #getVisualBounds()
     */
    double getWidth(); // getVisualBounds().width

    /**
     * Height of the drawable.
     * @see #getVisualBounds()
     */
    double getHeight(); // getVisualBounds().height

    @Override
    Matrix getTransform();

    /**
     * Sets the X-coordinate of the drawable.
     * @see #getX()
     * @see #setPos(double, double)
     */
    @Override
    void setX(double x); //Calls setPos

    /**
     * Sets the Y-coordinate of the drawable.
     * @see #getY()
     * @see #setPos(double, double)
     */
    @Override
    void setY(double y); //Calls setPos

    /**
     * Stretches the drawable to the given width/height.
     */
    @Override
    void setSize(double w, double h);

    /**
     * Moves and stretches this drawable to make it fit inside the specified bounding box.
     */
    @Override
    void setBounds(double x, double y, double w, double h);

    /**
     * Changes the Z-index.
     * @see #getZ()
     */
    void setZ(short z);

    /**
     * Sets the visibility flag.
     */
    void setVisible(boolean v);

    /**
     * Sets the blend mode.
     * @see #getBlendMode()
     */
    void setBlendMode(BlendMode m);

    /**
     * Enabled/disables clipping.
     * @see #isClipEnabled()
     */
    void setClipEnabled(boolean clip);

}
