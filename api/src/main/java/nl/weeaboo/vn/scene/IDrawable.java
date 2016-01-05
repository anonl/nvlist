package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawTransform;

public interface IDrawable extends IVisualElement, IColorizable, IDrawTransform {

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

    void setZ(short z);

    double getX();
    double getY();
    double getWidth(); // getVisualBounds().width
    double getHeight(); // getVisualBounds().height

    @Override
    Matrix getTransform();

	void setX(double x); //Calls setPos
	void setY(double y); //Calls setPos
	void setWidth(double w); //Calls setSize
	void setHeight(double h); //Calls setSize
    void translate(double dx, double dy); //Calls setPos
	void setPos(double x, double y);
	void setSize(double w, double h);

	/**
	 * Moves and stretches this drawable to make it fit inside the specified bounding box.
	 */
	void setBounds(double x, double y, double w, double h);

	void setVisible(boolean v);

	void setBlendMode(BlendMode m);
	void setClipEnabled(boolean clip);

}
