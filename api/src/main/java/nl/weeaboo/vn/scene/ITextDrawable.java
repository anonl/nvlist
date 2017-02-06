package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.text.ITextRenderState;

public interface ITextDrawable extends ITransformable, ITextRenderState {

    /**
     * Returns the speed at which text appears (in glyphs per frame).
     * @see #setTextSpeed(double)
     */
    double getTextSpeed();

    /**
     * Changes the speed at which text appears (in glyphs per frame).
     */
    void setTextSpeed(double speed);

}
