package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IInterpolator;

public interface ICrossFadeConfig extends Serializable {

    /**
     * The interpolator can be used to make the cross-fade non-linear.
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
