package nl.weeaboo.vn.impl.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void pack() {
        LayoutSize widthHint = LayoutSize.of(layout.getChildLayoutBounds().w);
        LayoutSize prefHeight = layout.calculateLayoutHeight(LayoutSizeType.PREF, widthHint);

        LOG.debug("Calculated packed size: {}x{}", widthHint, prefHeight);

        setUnscaledSize(widthHint.value(), prefHeight.value(getUnscaledHeight()));
    }

}
