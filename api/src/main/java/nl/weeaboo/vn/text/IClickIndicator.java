package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.ITextDrawable;

public interface IClickIndicator extends Serializable, IDestructible {

    void update(ITextDrawable textDrawable);

    IDrawable getDrawable();

    /**
     * Sets the click indicator's drawable. This drawable is controlled by the click indicator and used to
     * render itself.
     */
    void setDrawable(IDrawable d);

    double getWidth();
    double getHeight();
    
    void setSize(double w, double h);

}
