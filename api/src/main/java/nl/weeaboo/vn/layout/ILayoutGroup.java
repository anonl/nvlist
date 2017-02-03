package nl.weeaboo.vn.layout;

import nl.weeaboo.common.Rect2D;

/**
 * Represents a group of elements that can be positioned by a layout algorithm.
 */
public interface ILayoutGroup extends ILayoutElem {

    /** Removes a child element from this layout group. */
    void remove(ILayoutElem elem);

    /**
     * @return {@code true} if the layout is valid. Use {@link #layout()} to re-layout when invalid.
     */
    boolean isLayoutValid();

    /**
     * Marks the layout as 'dirty'.
     *
     * @see #isLayoutValid()
     * @see #layout()
     */
    void invalidateLayout();

    /** Applies the layout algorithm to this group and its sub-elements. */
    void layout();

    /**
     * @return The bounding rectangle within which child elements should be laid out.
     */
    Rect2D getChildLayoutBounds();

}
