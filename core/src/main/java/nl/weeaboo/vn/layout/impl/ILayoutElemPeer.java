package nl.weeaboo.vn.layout.impl;

import nl.weeaboo.common.Rect2D;

public interface ILayoutElemPeer {

    /**
     * @return {@code true} if this element is currently visible. This information is used by the layout
     *         system to skip over invisible items without reserving room for them.
     */
    boolean isVisible();

    /**
     * Positions this element within its parent's layout. This method is intended to only be called by the
     * layout system.
     */
    void setLayoutBounds(Rect2D rect);

    double getWidth();
    double getHeight();

}
