package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.vn.core.IDestructible;

/**
 * Raw pixel data which may be turned into a {@link ITexture} for rendering.
 */
public interface ITextureData extends Serializable, IDestructible {

    @Override
    @Deprecated
    void destroy();

    @Override
    @Deprecated
    boolean isDestroyed();

    /**
     * Pixel width of the texture data.
     */
    int getWidth();

    /**
     * Pixel height of the texture data.
     */
    int getHeight();

}
