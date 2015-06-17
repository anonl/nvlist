package nl.weeaboo.vn.image;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public interface IImageModule extends Serializable {

    public Entity createImage(ILayer layer);

    public Entity createTextDrawable(ILayer layer);

    public Entity createButton(ILayer layer);

    /**
     * Creates a texture object from the specified filename.
     *
     * @param loadInfo Filename of the requested resource and related metadata.
     * @param suppressErrors If <code>true</code> doesn't log any errors that may occur.
     */
    public ITexture getTexture(ResourceLoadInfo info, boolean suppressErrors);

    /**
     * Creates a texture from the given image data. The {@code scaleX} and {@code scaleY} factors scale from
     * pixel coordinates to the coordinates of image state.
     */
    public ITexture createTexture(ITextureData texData, double scaleX, double scaleY);

    /**
     * Creates a texture from a screenshot.
     */
    public ITexture createTexture(IScreenshot ss);

    /**
     * Returns the paths for all image files in the specified folder and its sub-folders.
     */
    public Collection<String> getImageFiles(String folder);

    /**
     * Changes the desired image resolution (width x height). Images are loaded from the resource folder that
     * most closely matches the desired size.
     */
    public void setImageResolution(Dim size);

}
