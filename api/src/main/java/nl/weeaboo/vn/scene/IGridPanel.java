package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.layout.GridCellConstraints;

public interface IGridPanel extends IPanel {

    /**
     * Add an element to the panel.
     */
    GridCellConstraints add(IVisualElement elem);

    /**
     * Ends the current row. Further elements will be placed in a line below the current elements.
     */
    void endRow();

    /**
     * Adjusts the size of the panel to fit its contents.
     */
    void pack();
}
