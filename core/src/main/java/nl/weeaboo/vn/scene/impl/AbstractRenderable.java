package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.core.impl.TransientListenerSupport;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IRenderable;

public abstract class AbstractRenderable implements IRenderable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final TransientListenerSupport changeHelper = new TransientListenerSupport();

    private double width = 1;
    private double height = 1;

    @Override
    public void onAttached(IEventListener cl) {
        if (cl != null) {
            changeHelper.addTransientListener(cl);
        }
    }

    @Override
    public void onDetached(IEventListener cl) {
        if (cl != null) {
            changeHelper.removeTransientListener(cl);
        }
    }

    @Override
    public void update() {
    }

    protected final void fireChanged() {
        changeHelper.fireListeners();
    }

    /** Sets the current width/height to the native width/height */
    protected void pack() {
        setSize(getNativeWidth(), getNativeHeight());
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double w, double h) {
        if (w <= 0 || h <= 0) {
            return;
        }

        if (width != w || height != h) {
            width = w;
            height = h;

            fireChanged();
        }
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.of(0, 0, getWidth(), getHeight());
    }

    @Override
    public final void render(IDrawBuffer drawBuffer, IDrawable parent, double dx, double dy) {
        Area2D bounds = Area2D.of(dx, dy, width, height);
        render(drawBuffer, parent, bounds);
    }

    protected abstract void render(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds);

}
