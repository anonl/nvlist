package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IDestructible;

public interface ITextureData extends Serializable, IDestructible {

    int getWidth();

    int getHeight();

}
