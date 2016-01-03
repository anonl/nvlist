package nl.weeaboo.vn.scene;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.IScreenshotBuffer;

public interface ILayer extends IVisualGroup {

    void add(IDrawable d);

	/**
	 * @return {@code true} if the specified layer is a descendant of this layer.
	 */
	boolean containsLayer(ILayer layer);

	/**
     * @return A read-only view of all layer children (non-recursive).
     */
    Iterable<? extends ILayer> getSubLayers();

	/**
	 * @return A buffer for pending screenshots. Screenshots requests queued in this buffer will be fullfilled
	 *         at some later time.
	 */
	IScreenshotBuffer getScreenshotBuffer();

    double getX();
    double getY();
    double getWidth();
    double getHeight();
    Rect2D getBounds();

	void setX(double x); //Calls setPos
	void setY(double y); //Calls setPos
	void setZ(short z);
	void setWidth(double w); //Calls setSize
	void setHeight(double h); //Calls setSize
	void setPos(double x, double y);
	void setSize(double w, double h);

	/** Simultaneously sets the size and pos of this layer */
	void setBounds(double x, double y, double w, double h);

	void setVisible(boolean v);

}
