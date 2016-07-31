package nl.weeaboo.vn.layout;

/**
 * Represents a group of elements that can be positioned by a layout algorithm.
 */
public interface ILayoutGroup extends ILayoutElem {

    /** @return {@code true} if the layout is considered 'dirty', requiring a {@link #layout()} to fix. */
    boolean isLayoutRequired();

    /**
     * Marks the layout as 'dirty'.
     *
     * @see #isLayoutRequired()
     * @see #layout()
     */
    void requireLayout();

    /** Applies the layout algorithm to this group and its sub-elements. */
    void layout();

}
