package nl.weeaboo.vn.core;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IRenderable;

public final class NullRenderer implements IRenderable {

    private static final long serialVersionUID = 1L;

    private static final NullRenderer INSTANCE = new NullRenderer();

    private NullRenderer() {
    }

    public static NullRenderer getInstance() {
        return INSTANCE;
    }

    /** Serialization hook */
    private Object readResolve() {
        return getInstance();
    }

    @Override
    public void onAttached(IChangeListener cl) {
    }

    @Override
    public void onDetached(IChangeListener cl) {
    }

    @Override
    public double getNativeWidth() {
        return 0;
    }

    @Override
    public double getNativeHeight() {
        return 0;
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.of(0, 0, getNativeWidth(), getNativeHeight());
    }

    @Override
    public void render(IDrawable parentComponent, Area2D bounds, IDrawBuffer drawBuffer) {
    }

}
