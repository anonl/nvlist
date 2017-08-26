package nl.weeaboo.vn.image;

import java.util.Collection;

import javax.annotation.Nullable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;

public interface IImageModule extends IModule, IResourceResolver {

    /**
     * Creates a new image drawable and adds it to the specified layer.
     */
    IImageDrawable createImage(ILayer layer);

    /**
     * Creates a new text drawable and adds it to the specified layer.
     */
    ITextDrawable createTextDrawable(ILayer layer);

    /**
     * Creates a new button drawable and adds it to the specified layer.
     */
    IButton createButton(ILayer layer, IScriptContext scriptContext);

    /**
     * Convenience method for {@link #getTexture(ResourceLoadInfo, boolean)}.
     */
    @Nullable ITexture getTexture(FilePath filename);

    /**
     * Attempts to load the texture with the specified filename.
     *
     * @param path Filename of the requested resource and related metadata.
     * @param suppressErrors If {@code true} doesn't log any errors that may occur.
     * @return The texture, or {@code null} if loading failed.
     */
    @Nullable ITexture getTexture(ResourceLoadInfo path, boolean suppressErrors);

    /**
     * Attempts to load a ninepatch based on the specified filename.
     *
     * @param path Filename of the requested resource and related metadata.
     * @param suppressErrors If {@code true} doesn't log any errors that may occur.
     * @return The ninepatch, or {@code null} if loading failed.
     */
    @Nullable INinePatch getNinePatch(ResourceLoadInfo path, boolean suppressErrors);

    /**
     * Creates a solid-color texture with the given color.
     */
    ITexture getColorTexture(int argb);

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
    Collection<FilePath> getImageFiles(FilePath folder);

    /**
     * Changes the desired image resolution (width x height). Images are loaded from the resource folder that
     * most closely matches the desired size.
     */
    void setImageResolution(Dim size);

    /**
     * Schedules a screenshot of the given layer. Use {@link IScreenshot#isAvailable()} and/or
     * {@link IScreenshot#isFailed()} to find out the state of the returned screenshot object.
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

    /**
     * Suggests to the resource loader that the image with the given filename should be preloaded into memory.
     * @param filename Path to the image file.
     */
    void preload(FilePath filename);

}
