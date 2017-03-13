package nl.weeaboo.vn.layout;

import java.io.Serializable;

import nl.weeaboo.common.Insets2D;

public interface IGridLayout extends Serializable {

    /** Adds an element to the layout */
    GridCellConstraints add(ILayoutElem elem);

    /** Ends the current row and starts a new one. */
    void endRow();

    /** Returns the number of rows currently in the layout. */
    int getRowCount();

    /** Returns the number of columns currently in the layout. */
    int getColCount();

    /** Sets the padding for the sides of the layout. */
    void setInsets(Insets2D insets);

    void setRowSpacing(double amount);

    void setColSpacing(double amount);

}
