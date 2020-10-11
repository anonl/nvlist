package nl.weeaboo.vn.impl.image;

import java.util.Collection;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.AbstractModule;
import nl.weeaboo.vn.impl.core.DefaultEnvironment;
import nl.weeaboo.vn.impl.image.ResolutionFolderSelector.ResolutionPath;
import nl.weeaboo.vn.impl.scene.Button;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.scene.TextDrawable;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;

/**
 * Default implementation of {@link IImageModule}.
 */
public class ImageModule extends AbstractModule implements IImageModule {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ImageModule.class);

    protected final IEnvironment env;
    protected final ImageResourceLoader resourceLoader;

    private final ITextureStore texStore;

    public ImageModule(DefaultEnvironment env) {
        this(env, new ImageResourceLoader(env));
    }

    public ImageModule(DefaultEnvironment env, ImageResourceLoader resourceLoader) {
        this.env = env;
        this.resourceLoader = resourceLoader;

        texStore = new TextureStore(resourceLoader);
        resourceLoader.setPreloadHandler(texStore);

        IRenderEnv renderEnv = env.getRenderEnv();
        setImageResolution(renderEnv.getVirtualSize());
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public IImageDrawable createImage(ILayer layer) {
        ImageDrawable image = new ImageDrawable();
        layer.add(image);
        return image;
    }

    @Override
    public ITextDrawable createTextDrawable(ILayer layer) {
        TextDrawable textDrawable = new TextDrawable(env.getTextModule().getFontStore());
        textDrawable.setSize(layer.getWidth(), layer.getHeight());
        layer.add(textDrawable);
        return textDrawable;
    }

    @Override
    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        Button button = new Button(scriptContext.getEventDispatcher(), env.getTextModule().getFontStore());
        layer.add(button);
        return button;
    }

    @Override
    public @Nullable ITexture getTexture(FilePath filename) {
        return getTexture(LuaScriptUtil.createLoadInfo(MediaType.IMAGE, filename), false);
    }

    @Override
    public @Nullable ITexture getTexture(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        FilePath path = loadInfo.getPath();
        resourceLoader.checkRedundantFileExt(path);

        ResourceId resourceId = resolveResource(path);
        if (resourceId == null) {
            if (!suppressErrors) {
                LOG.debug("Unable to find image file: " + path);
            }
            return null;
        }

        // Quick abort if we need an image def and it doesn't exist
        if (resourceId.hasSubId()) {
            IImageDefinition imageDef = resourceLoader.getImageDef(resourceId.getFilePath());
            if (imageDef == null || imageDef.findSubRect(resourceId.getSubId()) == null) {
                if (!suppressErrors) {
                    LOG.debug("Image definition not found: " + resourceId);
                }
                return null;
            }
        }

        return getTextureNormalized(resourceId, loadInfo);
    }

    @Override
    public @Nullable INinePatch getNinePatch(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        NinePatchLoader loader = new NinePatchLoader(this);
        return loader.loadNinePatch(loadInfo, suppressErrors);
    }

    /**
     * Is called from {@link #getTexture(ResourceLoadInfo, boolean)}
     */
    protected @Nullable ITexture getTextureNormalized(ResourceId resourceId, ResourceLoadInfo loadInfo) {
        resourceLoader.logLoad(resourceId, loadInfo);

        return texStore.getTexture(resourceId);
    }

    @Override
    public ITexture getColorTexture(int argb) {
        return texStore.getColorTexture(argb);
    }

    @Override
    public ITexture createTexture(ITextureData texData, double sx, double sy) {
        return ((IGdxTextureData)texData).toTexture(sx, sy);
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
    public IScreenshot screenshot() {
        IContext context = env.getContextManager().getPrimaryContext();
        if (context == null) {
            LOG.warn("Unable to take screenshot, no context is active");
            return EmptyScreenshot.getInstance();
        }
        return screenshot(context.getScreen().getRootLayer(), Short.MIN_VALUE, false, true);
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

    @Override
    public Collection<FilePath> getImageFiles(FilePath folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    @Override
    public void setImageResolution(Dim desiredSize) {
        ResolutionFolderSelector folderSelector = new ResolutionFolderSelector(env, MediaType.IMAGE);
        ResolutionPath selected = folderSelector.select(desiredSize);

        resourceLoader.setImageResolution(selected.folder, selected.resolution);
        texStore.clear();
    }

}
