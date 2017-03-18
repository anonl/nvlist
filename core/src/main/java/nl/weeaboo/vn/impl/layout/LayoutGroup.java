package nl.weeaboo.vn.impl.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.ILayoutGroup;

public abstract class LayoutGroup extends LayoutElem implements ILayoutGroup {

    private static final long serialVersionUID = LayoutImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(LayoutGroup.class);

    private Insets2D insets = Insets2D.EMPTY;
    private boolean layoutValid;

    /** True if we're currently inside the {@link #layout()} method. */
    private volatile boolean performingLayout;

    public LayoutGroup(ILayoutElemPeer visualElem) {
        super(visualElem);
    }

    @Override
    public final boolean isLayoutValid() {
        return layoutValid;
    }

    @Override
    public final void invalidateLayout() {
        layoutValid = false;
    }

    @Override
    public final void layout() {
        if (performingLayout) {
            LOG.warn("Skipping new layout operation, already busy performing a layout");
            return;
        }

        performingLayout = true;
        try {
            doLayout();
        } finally {
            performingLayout = false;
        }
        // Outside the finally block because we don't want to clear this flag in case of an exception
        layoutValid = true;
    }

    protected abstract void doLayout();

    @Override
    protected void onLayoutBoundsChanged(Rect2D rect) {
        invalidateLayout();
    }

    protected final double getChildLayoutWidth() {
        return Math.max(0, getLayoutWidth() - insets.getHorizontal());
    }

    protected final double getChildLayoutHeight() {
        return Math.max(0, getLayoutHeight() - insets.getVertical());
    }

    @Override
    public final Rect2D getChildLayoutBounds() {
        Rect2D r = getLayoutBounds();
        return Rect2D.of(r.x + insets.left, r.y + insets.top, getChildLayoutWidth(), getChildLayoutHeight());
    }

    /**
     * @return The insets between the layouts outer bounds and its contents.
     */
    public Insets2D getInsets() {
        return insets;
    }

    protected void setInsets(Insets2D i) {
        insets = Checks.checkNotNull(i);
    }

}
