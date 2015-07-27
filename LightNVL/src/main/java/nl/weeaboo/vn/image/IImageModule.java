package nl.weeaboo.vn.image;

import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public interface IImageModule extends IModule {

    Entity createImage(ILayer layer);

    Entity createTextDrawable(ILayer layer);

    Entity createButton(ILayer layer);

    /**
     * Creates a texture object from the specified filename.
     *
     * @param loadInfo Filename of the requested resource and related metadata.
     * @param suppressErrors If <code>true</code> doesn't log any errors that may occur.
     */
    ITexture getTexture(ResourceLoadInfo info, boolean suppressErrors);

    /**
     * Creates a texture from the given image data. The {@code scaleX} and {@code scaleY} factors scale from
     * pixel coordinates to the coordinates of image state.
     */
    ITexture createTexture(ITextureData texData, double scaleX, double scaleY);

    /**
     * Creates a texture from a screenshot.
     */
    ITexture createTexture(IScreenshot ss);

    /**
     * Returns the paths for all image files in the specified folder and its sub-folders.
     */
    Collection<String> getImageFiles(String folder);

    /**
     * Changes the desired image resolution (width x height). Images are loaded from the resource folder that
     * most closely matches the desired size.
     */
    void setImageResolution(Dim size);

    /**
     * Schedules a screenshot of the given layer. Use {@link IScreenshot#isAvailable()} and/or
     * {@link IScreenshot#isCancelled()} to find out the state of the returned screenshot object.
     *
     * @param layer The layer to take the screenshot in. Any layers beneath this layer will also be visible.
     * @param z The z-index at which to take the screenshot. Any objects in front of this z index will not be
     *        visible.
     * @param isVolatile If {@code true}, the pixels of the screenshot will be stored in volatile GPU memory
     *        only.
     * @param clipEnabled If {@code true}, honor the layer's clipping bounds. If {@code false}, a screenshot
     *        of the entire visible screen will be taken instead.
     */
    IScreenshot screenshot(ILayer layer, short z, boolean isVolatile, boolean clipEnabled);

}
