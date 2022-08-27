package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.scene.IImageDrawable;

/**
 * Renderable image data.
 *
 * @see IImageDrawable#setTexture(ITexture)
 * @see ITextureData
 */
public interface ITexture extends Serializable {

    Area2D DEFAULT_UV = Area2D.of(0, 0, 1, 1);

    /**
     * @return The texture width in image state coordinates.
     */
    double getWidth();

    /**
     * @return The texture height in image state coordinates.
     */
    double getHeight();

    /**
     * @return The texture width in pixels.
     */
    int getPixelWidth();

    /**
     * @return The texture height in pixels.
     */
    int getPixelHeight();

    /**
     * @return The scale factor from pixel size to virtual size.
     */
    double getScaleX();

    /**
     * @return The scale factor from pixel size to virtual size.
     */
    double getScaleY();

    /**
     * @return The texture mapping coordinates used by the underlying graphics system.
     */
    Area2D getUV();

}
