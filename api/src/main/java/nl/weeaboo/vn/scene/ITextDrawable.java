package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.text.IClickIndicator;
import nl.weeaboo.vn.text.ITextRenderState;

public interface ITextDrawable extends ITransformable, ITextRenderState {

    IClickIndicator getClickIndicator();

    double getTextSpeed();

    void setTextSpeed(double speed);

}
