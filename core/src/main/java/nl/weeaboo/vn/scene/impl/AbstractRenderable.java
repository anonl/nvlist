package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IChangeListener;
import nl.weeaboo.vn.core.impl.ChangeHelper;
import nl.weeaboo.vn.scene.IRenderable;

public abstract class AbstractRenderable implements IRenderable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final ChangeHelper changeHelper = new ChangeHelper();

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

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.of(0, 0, getNativeWidth(), getNativeHeight());
    }

}
