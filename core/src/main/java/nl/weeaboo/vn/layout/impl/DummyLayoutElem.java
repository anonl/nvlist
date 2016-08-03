package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public final class DummyLayoutElem extends LayoutElem {

    private static final long serialVersionUID = 1L;

    private LayoutSize prefWidth;
    private LayoutSize minWidth;
    private LayoutSize maxWidth;

    public DummyLayoutElem() {
        super(new DummyLayoutElemPeer());
    }

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

}
