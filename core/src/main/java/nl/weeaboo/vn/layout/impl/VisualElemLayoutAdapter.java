package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.scene.IVisualElement;

public class VisualElemLayoutAdapter extends AbstractLayoutElem {

    private static final long serialVersionUID = 1L;

    private IVisualElement visualPeer;

    public VisualElemLayoutAdapter(IVisualElement visualPeer) {
        this.visualPeer = Checks.checkNotNull(visualPeer);
    }

    @Override
    public boolean isVisible() {
        return visualPeer.isVisible();
    }

    @Override
    public void destroy() {
        visualPeer.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return visualPeer.isDestroyed();
    }

    @Override
    protected void onLayoutBoundsChanged(Rect2D rect) {
        // No default implementation possible
    }

}
