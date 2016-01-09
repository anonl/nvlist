package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IChangeListener;
import nl.weeaboo.vn.core.impl.ChangeHelper;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IRenderable;

public abstract class AbstractRenderable implements IRenderable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final ChangeHelper changeHelper = new ChangeHelper();

    private double width, height;

    @Override
    public void onAttached(IChangeListener cl) {
        changeHelper.addChangeListener(cl);
    }

    @Override
    public void onDetached(IChangeListener cl) {
        changeHelper.removeChangeListener(cl);
    }

    protected final void fireChanged() {
        changeHelper.fireChanged();
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
        if (width != w || height != h) {
            width = w;
            height = h;

            fireChanged();
        }
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.of(0, 0, getNativeWidth(), getNativeHeight());
    }

    @Override
    public final void render(IDrawable parent, double dx, double dy, IDrawBuffer drawBuffer) {
        Area2D bounds = Area2D.of(dx, dy, width, height);
        render(parent, bounds, drawBuffer);
    }

    protected abstract void render(IDrawable parent, Area2D bounds, IDrawBuffer drawBuffer);

}
