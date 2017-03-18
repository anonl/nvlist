package nl.weeaboo.vn.layout;

import java.io.Serializable;

import nl.weeaboo.common.Insets2D;

public interface IGridLayout extends Serializable {

    /**
     * Add an element to the panel.
     */
    GridCellConstraints add(ILayoutElem elem);

    /**
     * Ends the current row. Further elements will be placed in a line below the current elements.
     */
    void endRow();

    /** Returns the number of rows currently in the layout. */
    int getRowCount();

    /** Returns the number of columns currently in the layout. */
    int getColCount();

    /** Sets the padding for the sides of the layout. */
    void setInsets(Insets2D insets);

    /** Sets the amount of spacing between grid rows. */
    void setRowSpacing(double amount);

    /** Sets the amount of spacing between grid columns. */
    void setColSpacing(double amount);

}
