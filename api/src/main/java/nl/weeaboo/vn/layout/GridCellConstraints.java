package nl.weeaboo.vn.layout;

import java.io.Serializable;

public final class GridCellConstraints implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean growX = false;
    public boolean growY = false;

    /** Configures the constraints to grow in both directions. */
    public GridCellConstraints grow() {
        growX();
        growY();
        return this;
    }

    /** Configured the constraints to grow in the horizontal direction. */
    public GridCellConstraints growX() {
        growX = true;
        return this;
    }

    /** Configured the constraints to grow in the vertical direction. */
    public GridCellConstraints growY() {
        growY = true;
        return this;
    }

}
