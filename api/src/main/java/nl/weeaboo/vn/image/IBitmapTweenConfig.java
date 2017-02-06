package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IInterpolator;

public interface IBitmapTweenConfig extends Serializable {

    /**
     * Sets the width of the semi-transparent part of the transition.
     */
    void setRange(double range);

    /**
     * The interpolator can be used to remap alpha values. The default interpolator is non-linear in order to prevent
     * mach banding.
     */
    void setInterpolator(IInterpolator interpolator);

    /**
     * Sets the start texture for the transition.
     */
    void setStartTexture(ITexture texture);

    /**
     * @see #setStartTexture(ITexture)
     */
    void setStartTexture(ITexture texture, double alignX, double alignY);

    /**
     * Sets the end texture for the transition.
     */
    void setEndTexture(ITexture texture);

    /**
     * @see #setEndTexture(ITexture)
     */
    void setEndTexture(ITexture texture, double alignX, double alignY);

}
