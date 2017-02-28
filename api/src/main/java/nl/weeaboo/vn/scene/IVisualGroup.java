package nl.weeaboo.vn.scene;

public interface IVisualGroup extends IVisualElement {

    /**
     * @return {@code true} if this group contains the given element as a child.
     */
    boolean contains(IVisualElement elem);

    /**
     * @return A read-only view of all child elements contained in this group.
     */
    Iterable<? extends IVisualElement> getChildren();

    /**
     * Removes a previously added element.
     */
    void remove(IVisualElement elem);

}
