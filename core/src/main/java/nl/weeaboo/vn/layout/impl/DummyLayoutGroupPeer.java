package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.ILayoutGroupPeer;

final class DummyLayoutGroupPeer implements ILayoutGroupPeer {

    private static final long serialVersionUID = 1L;

    private boolean destroyed;

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
    }

}
