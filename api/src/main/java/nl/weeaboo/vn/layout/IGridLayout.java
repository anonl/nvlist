package nl.weeaboo.vn.layout;

import java.io.Serializable;

public interface IGridLayout extends Serializable {

    void add(ILayoutElem elem, GridCellConstraints constraints);

    /** Ends the current row and starts a new one */
    void endRow();

    int getRowCount();

    int getColCount();

}
