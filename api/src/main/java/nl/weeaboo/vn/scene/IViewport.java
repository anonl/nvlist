package nl.weeaboo.vn.scene;

/**
 * Viewport clips the rendering of its children to its own bounds.
 */
public interface IViewport extends IAxisAlignedContainer, IScrollable {

    /** Sets the viewport's content element. */
    void setContents(IVisualElement elem);

}
