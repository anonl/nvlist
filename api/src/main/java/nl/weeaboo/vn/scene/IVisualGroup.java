package nl.weeaboo.vn.scene;

public interface IVisualGroup extends IVisualElement {

    /**
     * @return A read-only view of all child elements contained in this group.
     */
    Iterable<? extends IVisualElement> getChildren();

}
