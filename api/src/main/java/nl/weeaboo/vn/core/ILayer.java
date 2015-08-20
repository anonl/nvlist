package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.image.IScreenshotBuffer;

public interface ILayer extends IDestructible, IEntityContainer, IRenderable, Serializable {

    /**
     * Creates a new entity and adds it to this layer.
     */
    Entity createEntity();

	/**
	 * @return {@code true} if the specified layer is a descendant of this layer.
	 */
	boolean containsLayer(ILayer layer);

	/**
	 * @return A read-only view of all sub-layers (non-recursive).
	 */
	Collection<? extends ILayer> getSubLayers();

    /**
     * @return A read-only view of all entities contained in this layer.
     */
    Iterable<Entity> getContents();

	/**
	 * @return A buffer for pending screenshots. Screenshots requests queued in this buffer will be fullfilled
	 *         at some later time.
	 */
	IScreenshotBuffer getScreenshotBuffer();

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
