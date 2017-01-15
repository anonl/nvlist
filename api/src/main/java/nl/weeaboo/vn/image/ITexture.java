package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;

public interface ITexture extends Serializable {

    public static Area2D DEFAULT_UV = Area2D.of(0, 0, 1, 1);

    /**
     * @return The texture width in image state coordinates.
     */
    public double getWidth();

    /**
     * @return The texture height in image state coordinates.
     */
    public double getHeight();

    /**
     * @return The texture width in pixels.
     */
    public int getPixelWidth();

    /**
     * @return The texture height in pixels.
     */
    public int getPixelHeight();

    /**
     * @return The scale factor from pixel size to virtual size.
     */
    public double getScaleX();

    /**
     * @return The scale factor from pixel size to virtual size.
     */
    public double getScaleY();

    /**
     * @return The texture mapping coordinates used by the underlying graphics system.
     */
    public Area2D getUV();

}
