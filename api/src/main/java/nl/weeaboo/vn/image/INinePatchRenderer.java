package nl.weeaboo.vn.image;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.scene.IRenderable;

public interface INinePatchRenderer extends IRenderable {

    /** Named regions of the 9-patch */
    public enum EArea {
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        BOTTOM_RIGHT;
    }

    /** @return The current texture for the requested region */
    ITexture getTexture(EArea area);

    /** Sets the texture of the specified region */
    void setTexture(EArea area, ITexture texture);

    /** @see #setInsets(Insets2D) */
    Insets2D getInsets();

    /** Sets the amount of non-resizable space on the top/right/bottom/left of the 9-patch */
    void setInsets(Insets2D i);

}
