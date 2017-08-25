package nl.weeaboo.vn.image.desc;

import java.util.Collection;

import javax.annotation.Nullable;

import nl.weeaboo.common.Dim;

public interface IImageDefinition {

    /**
     * Name of the file storing image definitions for images in the same folder.
     */
    public static final String IMG_DEF_FILE = "img.json";

    /**
     * The filename of the image (not a file path).
     */
    String getFilename();

    /**
     * @return The dimensions of the full image.
     */
    Dim getSize();

    /**
     * Returns a read-only view of all sub-rects defined for this image.
     */
    Collection<? extends IImageSubRect> getSubRects();

    /**
     * Returns the sub-rect with the given ID, or {@code null} if not found.
     */
    @Nullable IImageSubRect findSubRect(String id);

    /**
     * Interpolation method to use when downscaling the image.
     */
    GLScaleFilter getMinifyFilter();

    /**
     * Interpolation method to use when upscaling the image.
     */
    GLScaleFilter getMagnifyFilter();

    /**
     * Horizontal tiling mode.
     * <p>
     * This determines what happens when you attempt to render using UV-values outside the standard range
     * {@code [0, 1]}.
     */
    GLTilingMode getTilingModeX();

    /**
     * Vertical tiling mode.
     * <p>
     * This determines what happens when you attempt to render using UV-values outside the standard range
     * {@code [0, 1]}.
     */
    GLTilingMode getTilingModeY();

}
