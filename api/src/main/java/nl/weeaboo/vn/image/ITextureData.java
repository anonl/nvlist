package nl.weeaboo.vn.image;

import java.io.Serializable;

/**
 * Raw pixel data which may be turned into a {@link ITexture} for rendering.
 */
public interface ITextureData extends Serializable {

    /**
     * Pixel width of the texture data.
     */
    int getWidth();

    /**
     * Pixel height of the texture data.
     */
    int getHeight();

}
