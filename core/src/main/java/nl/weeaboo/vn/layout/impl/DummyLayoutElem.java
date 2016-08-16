package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public final class DummyLayoutElem extends LayoutElem {

    private static final long serialVersionUID = 1L;

    private LayoutSize prefWidth;
    private LayoutSize minWidth;
    private LayoutSize maxWidth;
    private LayoutSize prefHeight;
    private LayoutSize minHeight;
    private LayoutSize maxHeight;

    public DummyLayoutElem() {
        super(new DummyLayoutElemPeer());

        setLayoutWidths(0, 10, Double.POSITIVE_INFINITY);
        setLayoutHeights(0, 10, Double.POSITIVE_INFINITY);
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

    @Override
    public LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint) {
        switch (type) {
        case MIN:
            return minHeight;
        case PREF:
            return prefHeight;
        case MAX:
            return maxHeight;
        default:
            throw new IllegalArgumentException("Unsupported size type: " + type);
        }
    }

    public final void setLayoutWidths(double min, double pref, double max) {
        setLayoutWidths(LayoutSize.of(min), LayoutSize.of(pref), LayoutSize.of(max));
    }
    public final void setLayoutWidths(LayoutSize min, LayoutSize pref, LayoutSize max) {
        this.minWidth = min;
        this.prefWidth = pref;
        this.maxWidth = max;
    }

    public final void setLayoutHeights(double min, double pref, double max) {
        setLayoutHeights(LayoutSize.of(min), LayoutSize.of(pref), LayoutSize.of(max));
    }
    public final void setLayoutHeights(LayoutSize min, LayoutSize pref, LayoutSize max) {
        this.minHeight = min;
        this.prefHeight = pref;
        this.maxHeight = max;
    }

}
