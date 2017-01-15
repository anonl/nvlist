package nl.weeaboo.vn.scene;

import java.io.Serializable;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.render.IDrawBuffer;

public interface IRenderable extends Serializable, IUpdateable {

    /**
     * Called when this renderable is attached to a drawable in the scene.
     *
     * @param cl The change listener to add.
     */
    void onAttached(IEventListener cl);

    /**
     * Called when this renderable is detached from the scene. This method can be used to clean up any native
     * resources.
     *
     * @param cl The change listener to remove.
     */
    void onDetached(IEventListener cl);

    /**
     * @return The intrinsic width for this renderable.
     */
    double getNativeWidth();

    /**
     * @return The intrinsic height for this renderable.
     */
    double getNativeHeight();

    /**
     * @return The current width of this renderable.
     */
    double getWidth();

    /**
     * @return The current height of this renderable.
     */
    double getHeight();

    /**
     * Changes the renderable's current width/height.
     *
     * @see #getWidth()
     * @see #getHeight()
     */
    void setSize(double w, double h);

    /**
     * @return The axis-aligned bounding box for this renderable element.
     */
    Rect2D getVisualBounds();

    /**
     * Renders to the given draw buffer
     */
    void render(IDrawBuffer drawBuffer, IDrawable parentComponent, double dx, double dy);

}
