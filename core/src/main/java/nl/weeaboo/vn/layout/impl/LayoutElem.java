package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.impl.Indirect;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;
import nl.weeaboo.vn.scene.impl.BoundsHelper;

public class LayoutElem implements ILayoutElem {

    private static final long serialVersionUID = LayoutImpl.serialVersionUID;

    private final Indirect<ILayoutElemPeer> visualElemRef;
    private final BoundsHelper layoutBounds = new BoundsHelper();

    public LayoutElem(ILayoutElemPeer visualElem) {
        this.visualElemRef = Indirect.of(Checks.checkNotNull(visualElem));
    }

    @Override
    public boolean isVisible() {
        return visualElemRef.get().isVisible();
    }

    @Override
    public LayoutSize calculateLayoutWidth(LayoutSizeType type, LayoutSize heightHint) {
        ILayoutElemPeer visualElem = visualElemRef.get();

        switch (type) {
        case MIN:
            return LayoutSize.ZERO;
        case PREF:
            return LayoutSize.of(visualElem.getWidth());
        case MAX:
            return LayoutSize.INFINITE;
        default:
            throw new IllegalArgumentException("Unknown size type: " + type);
        }
    }

    @Override
    public LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint) {
        ILayoutElemPeer visualElem = visualElemRef.get();

        switch (type) {
        case MIN:
            return LayoutSize.ZERO;
        case PREF:
            return LayoutSize.of(visualElem.getHeight());
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
    protected void onLayoutBoundsChanged(Rect2D rect) {
        ILayoutElemPeer visualElem = visualElemRef.get();

        visualElem.setLayoutBounds(rect);
    }

}
