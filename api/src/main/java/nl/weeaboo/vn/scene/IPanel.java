package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.image.INinePatch;

public interface IPanel extends IVisualGroup, IDrawable {

    /**
     * Sets the optional panel background.
     */
    void setBackground(INinePatch ninePatch);

}
