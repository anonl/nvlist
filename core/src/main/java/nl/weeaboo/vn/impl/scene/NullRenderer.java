package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;

public final class NullRenderer extends AbstractRenderable {

    private static final long serialVersionUID = 1L;

    @Override
    public double getNativeWidth() {
        return 0;
    }

    @Override
    public double getNativeHeight() {
        return 0;
    }

    @Override
    protected void render(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
    }

}
