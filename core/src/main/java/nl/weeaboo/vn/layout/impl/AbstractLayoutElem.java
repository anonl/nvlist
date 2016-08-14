package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;
import nl.weeaboo.vn.scene.impl.BoundsHelper;

public abstract class AbstractLayoutElem implements ILayoutElem {

    private static final long serialVersionUID = LayoutImpl.serialVersionUID;
    private static final LayoutSize DEFAULT_SIZE = LayoutSize.of(10);

    private final BoundsHelper layoutBounds = new BoundsHelper();

    @Override
    public LayoutSize calculateLayoutWidth(LayoutSizeType type, LayoutSize heightHint) {
        switch (type) {
        case MIN:
            return LayoutSize.ZERO;
        case PREF:
            return DEFAULT_SIZE;
        case MAX:
            return LayoutSize.INFINITE;
        default:
            throw new IllegalArgumentException("Unknown size type: " + type);
        }
    }

    @Override
    public LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint) {
        switch (type) {
        case MIN:
            return LayoutSize.ZERO;
        case PREF:
            return DEFAULT_SIZE;
        case MAX:
            return LayoutSize.INFINITE;
        default:
            throw new IllegalArgumentException("Unknown size type: " + type);
        }
    }

    /**
     * Convenience method for accessing the current layout width.
     *
     * @see #getLayoutBounds()
     */
    protected final double getLayoutWidth() {
        return layoutBounds.getWidth();
    }

    /**
     * Convenience method for accessing the current layout height.
     *
     * @see #getLayoutBounds()
     */
    protected final double getLayoutHeight() {
        return layoutBounds.getHeight();
    }

    @Override
    public final Rect2D getLayoutBounds() {
        return layoutBounds.getBounds();
    }

    @Override
    public final void setLayoutBounds(Rect2D rect) {
        if (!getLayoutBounds().equals(rect)) {
            layoutBounds.setBounds(rect);

            onLayoutBoundsChanged(rect);
        }
    }

    /**
     * @param rect The new layout bounds.
     */
    protected abstract void onLayoutBoundsChanged(Rect2D rect);

}
