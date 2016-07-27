package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.text.ITextRenderState;

public interface ITextDrawable extends ITransformable, ITextRenderState {

    double getTextSpeed();

    void setTextSpeed(double speed);

}
