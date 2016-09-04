package nl.weeaboo.vn.image.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.scene.impl.ComponentFactory;
import nl.weeaboo.vn.script.IScriptContext;

public class ImageModule implements IImageModule {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ImageModule.class);

    protected final IEnvironment env;
    protected final FileResourceLoader resourceLoader;
    protected final ComponentFactory entityHelper;

    private final TextureManager texManager;

    private Dim imageResolution;

    public ImageModule(DefaultEnvironment env) {
        this(env, new ImageResourceLoader(env));
    }

    public ImageModule(DefaultEnvironment env, FileResourceLoader resourceLoader) {
        this.env = env;
        this.resourceLoader = resourceLoader;
        this.entityHelper = new ComponentFactory();

        IRenderEnv renderEnv = env.getRenderEnv();
        imageResolution = renderEnv.getVirtualSize();

        texManager = new TextureManager(resourceLoader);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public ResourceId resolveResource(FilePath filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public IImageDrawable createImage(ILayer layer) {
        return entityHelper.createImage(layer);
    }

    @Override
    public ITextDrawable createTextDrawable(ILayer layer) {
        return entityHelper.createText(layer);
    }

    @Override
    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        return entityHelper.createButton(layer, scriptContext);
    }

    @Override
    public ITexture getTexture(FilePath filename) {
        return getTexture(new ResourceLoadInfo(filename), false);
    }

    @Override
    public ITexture getTexture(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        FilePath path = loadInfo.getPath();
        resourceLoader.checkRedundantFileExt(path);

        ResourceId resourceId = resourceLoader.resolveResource(path);
        if (resourceId == null) {
            if (!suppressErrors) {
                LOG.debug("Unable to find image file: " + path);
            }
            return null;
        }

        return getTextureNormalized(resourceId, loadInfo);
    }

    @Override
    public INinePatch getNinePatch(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        NinePatchLoader loader = new NinePatchLoader(this);
        return loader.loadNinePatch(loadInfo, suppressErrors);
    }

    /**
     * Is called from {@link #getTexture(ResourceLoadInfo, boolean)}
     */
    protected ITexture getTextureNormalized(ResourceId resourceId, ResourceLoadInfo loadInfo) {
        resourceLoader.logLoad(resourceId, loadInfo);

        double scale = getImageScale();
        return texManager.getTexture(resourceId, scale, scale);
    }

    @Override
    public ITexture createTexture(int colorARGB, int width, int height, double sx, double sy) {
        return texManager.generateTexture(colorARGB, Dim.of(width, height), sx, sy);
    }

    @Override
    public ITexture createTexture(ITextureData texData, double sx, double sy) {
        return texManager.generateTexture((IGdxTextureData)texData, sx, sy);
    }

    @Override
    public ITexture createTexture(IScreenshot ss) {
        IRenderEnv renderEnv = env.getRenderEnv();
        double sx = renderEnv.getWidth() / (double)ss.getScreenSize().w;
        double sy = renderEnv.getHeight() / (double)ss.getScreenSize().h;
        ITextureData pixels = ss.getPixels();
        return createTexture(pixels, sx, sy);
    }

    @Override
    public IScreenshot screenshot(ILayer layer, short z, boolean isVolatile, boolean clipEnabled) {
        IWritableScreenshot ss = new WritableScreenshot(z, isVolatile);
        layer.getScreenshotBuffer().add(ss, clipEnabled);
        return ss;
    }

    @Override
    public void preload(FilePath filename) {
        resourceLoader.preload(filename);
    }

    protected void onImageScaleChanged() {
        texManager.invalidateImageDefinitions();
    }

    @Override
    public Collection<FilePath> getImageFiles(FilePath folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    protected double getImageScale() {
        IRenderEnv renderEnv = env.getRenderEnv();
        return Math.min(renderEnv.getWidth() / (double)imageResolution.w,
                renderEnv.getHeight() / (double)imageResolution.h);
    }

    @Override
    public void setImageResolution(Dim size) {
        size = Checks.checkNotNull(size);
        if (!imageResolution.equals(size)) {
            imageResolution = size;
            onImageScaleChanged();
        }
    }

}
