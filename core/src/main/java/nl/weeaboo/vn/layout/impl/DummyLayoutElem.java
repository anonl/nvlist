package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public final class DummyLayoutElem extends AbstractLayoutElem {

    private static final long serialVersionUID = 1L;

    private boolean visible;
    private boolean destroyed;

    private LayoutSize prefWidth;
    private LayoutSize minWidth;
    private LayoutSize maxWidth;

    @Override
    public LayoutSize calculateLayoutWidth(LayoutSizeType type, LayoutSize heightHint) {
        switch (type) {
        case MIN:
            return minWidth;
        case PREF:
            return prefWidth;
        case MAX:
            return maxWidth;
        default:
            throw new IllegalArgumentException("Unsupported size type: " + type);
        }
    }

    public void setLayoutWidths(double min, double pref, double max) {
        setLayoutWidths(LayoutSize.of(min), LayoutSize.of(pref), LayoutSize.of(max));
    }
    public void setLayoutWidths(LayoutSize min, LayoutSize pref, LayoutSize max) {
        this.minWidth = min;
        this.prefWidth = pref;
        this.maxWidth = max;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    protected void onLayoutBoundsChanged(Rect2D rect) {
    }

}
