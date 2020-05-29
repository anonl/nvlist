package nl.weeaboo.vn.scene;

/**
 * Group of visual elements.
 */
public interface IVisualGroup extends IVisualElement {

    /**
     * Returns {@code true} if this group contains the given element as a child.
     */
    boolean contains(IVisualElement elem);

    /**
     * Returns a read-only view of all child elements contained in this group.
     */
    Iterable<? extends IVisualElement> getChildren();

    /**
     * Removes a previously added element.
     */
    void remove(IVisualElement elem);

}
