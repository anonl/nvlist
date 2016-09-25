package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.image.INinePatch;

public interface IPanel extends IVisualGroup, IDrawable {

    void setBackground(INinePatch ninePatch);

}
