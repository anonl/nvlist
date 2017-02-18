package nl.weeaboo.vn.impl.layout;

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
        }

        throw new IllegalArgumentException("Unsupported size type: " + type);
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
        }

        throw new IllegalArgumentException("Unsupported size type: " + type);
    }

    /**
     * Sets the minimum/preferred/maximum widths at once.
     */
    public final void setLayoutWidths(double min, double pref, double max) {
        setLayoutWidths(LayoutSize.of(min), LayoutSize.of(pref), LayoutSize.of(max));
    }

    /**
     * Sets the minimum/preferred/maximum widths at once.
     */
    public final void setLayoutWidths(LayoutSize min, LayoutSize pref, LayoutSize max) {
        this.minWidth = min;
        this.prefWidth = pref;
        this.maxWidth = max;
    }

    /**
     * Sets the minimum/preferred/maximum heights at once.
     */
    public final void setLayoutHeights(double min, double pref, double max) {
        setLayoutHeights(LayoutSize.of(min), LayoutSize.of(pref), LayoutSize.of(max));
    }

    /**
     * Sets the minimum/preferred/maximum heights at once.
     */
    public final void setLayoutHeights(LayoutSize min, LayoutSize pref, LayoutSize max) {
        this.minHeight = min;
        this.prefHeight = pref;
        this.maxHeight = max;
    }

}
