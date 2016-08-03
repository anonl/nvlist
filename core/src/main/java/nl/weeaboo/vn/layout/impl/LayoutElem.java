package nl.weeaboo.vn.layout.impl;

import com.google.common.base.Objects;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.ILayoutElemPeer;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;
import nl.weeaboo.vn.scene.impl.BoundsHelper;

public class LayoutElem implements ILayoutElem {

    private static final long serialVersionUID = LayoutImpl.serialVersionUID;
    private static final LayoutSize DEFAULT_SIZE = LayoutSize.of(10);

    private final BoundsHelper layoutBounds = new BoundsHelper();

    private final ILayoutElemPeer peer;

    public LayoutElem(ILayoutElemPeer peer) {
        this.peer = Checks.checkNotNull(peer);
    }

    @Override
    public boolean contains(ILayoutElemPeer peer) {
        return Objects.equal(this.peer, peer);
    }

    @Override
    public void destroy() {
        peer.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return peer.isDestroyed();
    }

    @Override
    public boolean isVisible() {
        return peer.isVisible();
    }

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
    protected void onLayoutBoundsChanged(Rect2D rect) {
        peer.setLayoutBounds(rect);
    }

}
