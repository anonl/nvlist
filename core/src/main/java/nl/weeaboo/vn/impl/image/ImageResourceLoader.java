package nl.weeaboo.vn.impl.image;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionCache;
import nl.weeaboo.vn.render.IRenderEnv;

final class ImageResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final IEnvironment env;
    private Dim imageResolution;

    private transient @Nullable ImageDefinitionCache cachedImageDefs;

    public ImageResourceLoader(IEnvironment env) {
        super(env, MediaType.IMAGE);

        this.env = env;
        this.imageResolution = env.getRenderEnv().getVirtualSize();

        setAutoFileExts("ktx", "jpg",
                "png", "pre.png",
                "jng", "pre.jng");
    }

    /**
     * Returns the image definition corresponding to the specified audio file, or {@code null} if it doesn't
     * exist or doesn't have an image definition.
     */
    @CheckForNull
    public final IImageDefinition getImageDef(FilePath filePath) {
        return getImageDefinitions().getMetaData(filePath);
    }

    private ImageDefinitionCache getImageDefinitions() {
        ImageDefinitionCache result = cachedImageDefs;
        if (result == null) {
            result = new ImageDefinitionCache(getFileSystem());
            cachedImageDefs = result;
        }
        return result;
    }

    /**
     * The scale factor from image resolution to the (fixed) virtual size.
     */
    double getImageScale() {
        IRenderEnv renderEnv = env.getRenderEnv();
        Dim virtualSize = renderEnv.getVirtualSize();
        return Math.min(virtualSize.w / (double)imageResolution.w,
                virtualSize.h / (double)imageResolution.h);
    }

    @Override
    protected void onResourceFolderChanged() {
        super.onResourceFolderChanged();

        cachedImageDefs = null;
    }

    public void setImageResolution(FilePath folder, Dim resolution) {
        this.setResourceFolder(folder);

        this.imageResolution = resolution;
    }
}
