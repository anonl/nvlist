package nl.weeaboo.vn.layout;

import java.io.Serializable;

import nl.weeaboo.common.Dim2D;
import nl.weeaboo.common.Rect2D;

public interface ILayoutElem extends Serializable {

    /**
     * @return {@code true} if this element is currently visible. This information is used by the layout
     *         system to skip over invisible items without reserving room for them.
     */
    boolean isVisible();

    /** @return The configured size for the specified layout size type (min/pref/max) */
    Dim2D getLayoutSize(SizeType type);

    /** @see #getLayoutSize(SizeType) */
    Dim2D getMinSize();

    /** @see #getLayoutSize(SizeType) */
    Dim2D getPrefSize();

    /** @see #getLayoutSize(SizeType) */
    Dim2D getMaxSize();

    /** @return The current position and size of this element within its parent layout. */
    Rect2D getLayoutBounds();

    /**
     * Positions this element within its parent's layout. This method is intended to only be called by the
     * layout system.
     */
    void setLayoutBounds(Rect2D rect);

}
