package nl.weeaboo.vn.impl.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.cache.CacheLoader;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.gdx.graphics.ColorTextureLoader;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.GeneratedResourceStore;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.gdx.res.TransformedResource;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Does the heavy lifting related to texture loading and generation for {@link IImageModule}.
 */
final class TextureManager implements Serializable {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

    private final StaticRef<GdxTextureStore> textureStore = StaticEnvironment.TEXTURE_STORE;
    private final StaticRef<GeneratedResourceStore> generatedTextureStore = StaticEnvironment.GENERATED_RESOURCES;

    private final FileResourceLoader resourceLoader;
    private final Dim virtualSize;
    private Dim imageResolution;

    private transient TextureCache textureCache;
    private transient ImageDefinitionCache cachedImageDefs;

    public TextureManager(FileResourceLoader resourceLoader, Dim virtualSize) {
        this.resourceLoader = resourceLoader;
        this.virtualSize = virtualSize;
        this.imageResolution = virtualSize;
    }

    public void setImageResolution(Dim size) {
        size = Checks.checkNotNull(size);
        if (!imageResolution.equals(size)) {
            imageResolution = size;

            // Image resolution changed
            cachedImageDefs = null;
        }
    }

    final IImageDefinition getImageDef(FilePath relPath) {
        if (cachedImageDefs == null) {
            cachedImageDefs = new ImageDefinitionCache(resourceLoader);
        }
        return cachedImageDefs.getImageDef(relPath);
    }

    public @Nullable ITexture getTexture(ResourceId resourceId) {
        if (textureCache == null) {
            textureCache = new TextureCache(new CacheLoader<ResourceId, ITexture>() {
                @Override
                public ITexture load(ResourceId resourceId) throws Exception {
                    return loadTexture(resourceId);
                }
            });
        }
        return textureCache.getTexture(resourceId);
    }

    /**
     * @return The loaded texture (never {@code null}).
     * @throws IOException If loading the texture failed.
     */
    private ITexture loadTexture(ResourceId resourceId) throws IOException {
        FilePath relPath = resourceId.getFilePath();
        FilePath absolutePath = resourceLoader.getAbsolutePath(relPath);
        IResource<Texture> res = textureStore.get().get(absolutePath);
        if (res == null) {
            throw new FileNotFoundException("Texture resource not found: " + absolutePath);
        }

        double scale = getImageScale();

        IImageDefinition imageDef = getImageDef(relPath);
        if (imageDef == null) {
            if (resourceId.hasSubId()) {
                LOG.warn("Image definition not found: {}", relPath);
                throw new FileNotFoundException("Texture sub-rect not found (missing image definition): "
                        + resourceId);
            }

            LOG.trace("Image definition not found: {}", relPath);
        } else {
            LOG.trace("Image definition found: {}", relPath);
            if (resourceId.hasSubId()) {
                IImageSubRect subRect = imageDef.findSubRect(resourceId.getSubId());
                if (subRect != null) {
                    LOG.debug("Load image sub-rect: {}: {}", resourceId, subRect.getArea());
                    return newTexture(new RegionResource(res, subRect.getArea()), scale, scale);
                } else {
                    LOG.warn("Image definition sub-rect not found: {}", resourceId);
                    throw new FileNotFoundException("Texture sub-rect not found: " + resourceId);
                }
            }
        }
        return newTexture(new RegionResource(res), scale, scale);
    }

    protected double getImageScale() {
        return Math.min(virtualSize.w / (double)imageResolution.w,
                virtualSize.h / (double)imageResolution.h);
    }

    public IResource<TextureRegion> generateTextureRegion(IGdxTextureData texData) {
        GeneratedResourceStore generatedStore = generatedTextureStore.get();
        return new GeneratedRegionResource(generatedStore.register(texData));
    }

    public ITexture generateTexture(IGdxTextureData texData, double sx, double sy) {
        return newTexture(generateTextureRegion(texData), sx, sy);
    }

    /**
     * @param argb ARGB8888, unassociated alpha
     */
    public ITexture getColorTexture(int argb) {
        String filename = ColorTextureLoader.getFilename(argb);
        ResourceId resourceId = new ResourceId(MediaType.IMAGE, FilePath.of(filename));

        ITexture texture = getTexture(resourceId);
        return Checks.checkNotNull(texture, "Color texture loading should never fail");
    }

    private static ITexture newTexture(IResource<TextureRegion> tr, double sx, double sy) {
        return new TextureAdapter(tr, sx, sy);
    }

    private static class RegionResource extends TransformedResource<Texture, TextureRegion> {

        private static final long serialVersionUID = 1L;

        /** May be null */
        private final Area subRect;

        public RegionResource(IResource<Texture> inner) {
            super(inner);

            this.subRect = null;
        }

        public RegionResource(IResource<Texture> inner, Area subRect) {
            super(inner);

            this.subRect = Checks.checkNotNull(subRect);
        }

        @Override
        protected TextureRegion transform(Texture original) {
            if (subRect == null) {
                return GdxTextureUtil.newGdxTextureRegion(original);
            } else {
                return GdxTextureUtil.newGdxTextureRegion(original, subRect);
            }
        }

    }

    private static class GeneratedRegionResource extends TransformedResource<IGdxTextureData, TextureRegion> {

        private static final long serialVersionUID = 1L;

        public GeneratedRegionResource(IResource<IGdxTextureData> inner) {
            super(inner);
        }

        @Override
        protected TextureRegion transform(IGdxTextureData original) {
            return original.toTextureRegion();
        }

    }

}
