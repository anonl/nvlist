package nl.weeaboo.vn.layout;

import java.io.Serializable;

import nl.weeaboo.common.Rect2D;

public interface ILayoutElem extends Serializable {

    /**
     * @return {@code true} if this element is currently visible. This information is used by the layout
     *         system to skip over invisible items without reserving room for them.
     */
    boolean isVisible();

    /**
     * @param heightHint The height for which to calculate the corresponding layout width. May be
     *        {@link LayoutSize#INFINITE} or {@link LayoutSize#UNKNOWN}, but never {@code null}.
     */
    LayoutSize calculateLayoutWidth(LayoutSizeType type, LayoutSize heightHint);

    /**
     * @param widthHint The width for which to calculate the corresponding layout height. May be
     *        {@link LayoutSize#INFINITE} or {@link LayoutSize#UNKNOWN}, but never {@code null}.
     */
    LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint);

    /** Returns the current position and size of this element within its parent layout. */
    Rect2D getLayoutBounds();

    /**
     * Positions this element within its parent's layout. This method is intended to only be called by the
     * layout system.
     */
    void setLayoutBounds(Rect2D rect);

}
