package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.vn.layout.GridCellConstraints;
import nl.weeaboo.vn.layout.impl.GridLayout;
import nl.weeaboo.vn.scene.IGridPanel;
import nl.weeaboo.vn.scene.IVisualElement;

public class GridPanel extends Panel implements IGridPanel {

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
    public void add(IVisualElement elem, GridCellConstraints constraints) {
        layout.add(elem.getLayoutAdapter(), constraints);
        add(elem);
    }

    @Override
    public void endRow() {
        layout.endRow();
    }

    @Override
    public GridCellConstraints newConstraints() {
        return new GridCellConstraints();
    }

}
