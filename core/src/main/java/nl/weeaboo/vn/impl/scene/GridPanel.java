package nl.weeaboo.vn.impl.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.impl.core.AlignUtil;
import nl.weeaboo.vn.impl.layout.GridLayout;
import nl.weeaboo.vn.layout.GridCellConstraints;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;
import nl.weeaboo.vn.scene.IGridPanel;
import nl.weeaboo.vn.scene.IVisualElement;

public class GridPanel extends Panel implements IGridPanel {

    private static final Logger LOG = LoggerFactory.getLogger(GridPanel.class);
    private static final long serialVersionUID = 1L;

    private final GridLayout layout;

    public GridPanel() {
        layout = new GridLayout(this);
    }

    @Override
    protected GridLayout getLayout() {
        return layout;
    }

    @Override
    public GridCellConstraints add(IVisualElement elem) {
        GridCellConstraints constraints = layout.add(elem.getLayoutAdapter());

        addChild(elem);

        return constraints;
    }

    @Override
    public void remove(IVisualElement elem) {
        removeChild(elem);

        layout.remove(elem.getLayoutAdapter());
    }

    @Override
    public void endRow() {
        layout.endRow();
    }

    @Override
    public final void pack(int anchor) {
        pack(anchor == 0 ? Direction.CENTER : Direction.fromInt(anchor));
    }

    @Override
    public void pack(Direction anchor) {
        anchor = MoreObjects.firstNonNull(anchor, Direction.CENTER);

        double oldWidth = getWidth();
        double oldHeight = getHeight();

        Rect2D oldChildLayoutBounds = layout.getChildLayoutBounds();
        LayoutSize widthHint = LayoutSize.of(oldChildLayoutBounds.w);
        LayoutSize prefHeight = layout.calculateLayoutHeight(LayoutSizeType.PREF, widthHint);
        LayoutSize prefWidth = layout.calculateLayoutWidth(LayoutSizeType.PREF, prefHeight);

        LOG.debug("Measured packed child contents size: {}x{}", prefWidth, prefHeight);

        Insets2D insets = layout.getInsets();
        double newLayoutWidth = insets.getHorizontal() + prefWidth.value(oldChildLayoutBounds.w);
        double newLayoutHeight = insets.getVertical() + prefHeight.value(oldChildLayoutBounds.h);
        setUnscaledSize(newLayoutWidth, newLayoutHeight);

        double dx = AlignUtil.alignAnchorX(oldWidth, getWidth(), anchor);
        double dy = AlignUtil.alignAnchorY(oldHeight, getHeight(), anchor);
        translate(dx, dy);
    }

    @Override
    public void setInsets(Insets2D insets) {
        layout.setInsets(insets);
    }

    @Override
    public void setRowSpacing(double amount) {
        layout.setRowSpacing(amount);
    }

    @Override
    public void setColSpacing(double amount) {
        layout.setColSpacing(amount);
    }

}
