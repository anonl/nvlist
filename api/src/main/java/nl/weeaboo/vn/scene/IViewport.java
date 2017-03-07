package nl.weeaboo.vn.scene;

public interface IViewport extends IAxisAlignedContainer, IScrollable {

    /** Sets the viewport's content element. */
    void setContents(IVisualElement elem);

}
