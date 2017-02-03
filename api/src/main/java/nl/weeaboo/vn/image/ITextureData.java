package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IDestructible;

public interface ITextureData extends Serializable, IDestructible {

    /**
     * Pixel width of the texture data.
     */
    int getWidth();

    /**
     * Pixel height of the texture data.
     */
    int getHeight();

}
