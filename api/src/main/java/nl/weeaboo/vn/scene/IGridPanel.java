package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.core.Direction;
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

    /** Sets the amount of spacing between grid rows. */
    void setRowSpacing(double amount);

    /** Sets the amount of spacing between grid columns. */
    void setColSpacing(double amount);

    /**
     * @see #pack(Direction)
     */
    void pack(int anchor);

    /** Adjusts the size of the panel to fit its contents, aligning the panel based on the given direction. */
    void pack(Direction anchor);

}
