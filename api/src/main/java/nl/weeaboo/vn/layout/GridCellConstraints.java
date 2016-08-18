package nl.weeaboo.vn.layout;

import java.io.Serializable;

public final class GridCellConstraints implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean growX = false;
    public boolean growY = false;

    public GridCellConstraints grow() {
        growX();
        growY();
        return this;
    }

    public GridCellConstraints growX() {
        growX = true;
        return this;
    }

    public GridCellConstraints growY() {
        growY = true;
        return this;
    }

}
