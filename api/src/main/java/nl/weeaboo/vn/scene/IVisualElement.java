package nl.weeaboo.vn.scene;

import java.io.Serializable;

import javax.annotation.Nullable;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.input.IInputHandler;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.signal.ISignalHandler;

public interface IVisualElement extends Serializable, IDestructible, ISignalHandler, IInputHandler {

    /**
     * Appends a new signal handler.
     * @param order Handlers are called in ascending order whenever a signal is received.
     */
    <T extends ISignalHandler & Serializable> void addSignalHandler(int order, T handler);

    /**
     * Removes the first occurrence of the given signal handler
     */
    void removeSignalHandler(ISignalHandler handler);

    /** Draws all visible items into the supplied draw buffer. */
    void draw(IDrawBuffer drawBuffer);

    /**
     * @return The parent of this element in the view hierarchy. For example, a {@link ILayer}.
     */
    @Nullable IVisualGroup getParent();

    /**
     * Attaches this element to the given parent.
     * <p>
     * If the parent is {@code null}, this element becomes detached from the view hierarchy. Detached elements no longer
     * receive signals.
     *
     * @param parent May be null.
     */
    void setParent(@Nullable IVisualGroup parent);

    /**
     * @return The relative rendering order of this element. Elements with lower Z-values are rendered on top of
     *         elements with higher Z-values.
     */
    short getZ();

    /**
     * @return {@code true} if this element should be rendered.
     */
    boolean isVisible();

    /**
     * @return The axis-aligned bounding box for this visual element, relative to the origin of its parent.
     */
    Rect2D getVisualBounds();

    /**
     * Returns information about the rendering environment.
     */
    @Nullable IRenderEnv getRenderEnv();

    /**
     * @return The interface used to access this visual element's layout properties.
     */
    ILayoutElem getLayoutAdapter();

}
