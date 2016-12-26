package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IInterpolator;

public interface ICrossFadeConfig extends Serializable {

    void setInterpolator(IInterpolator interpolator);

    void setStartTexture(ITexture texture);
    void setStartTexture(ITexture texture, double alignX, double alignY);

    void setEndTexture(ITexture texture);
    void setEndTexture(ITexture texture, double alignX, double alignY);

}
