package nl.weeaboo.vn.scene;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatch;

public interface IPanel extends IVisualGroup, IDrawable {

    /**
     * Sets the optional panel background.
     */
    void setBackground(INinePatch ninePatch);

    /**
     * @see #setInsets(Insets2D)
     */
    void setInsets(double top, double right, double bottom, double left);

    /** Sets the padding for the sides of the panel's layout. */
    void setInsets(Insets2D insets);

}
