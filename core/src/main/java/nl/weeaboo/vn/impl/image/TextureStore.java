package nl.weeaboo.vn.impl.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.Duration;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.gdx.graphics.ColorTextureLoader;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.gdx.res.TransformedResource;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;
import nl.weeaboo.vn.impl.core.DurationLogger;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Loads and caches {@link ITexture} objects. Creation of the underlying GDX {@link Texture} resources is
 * delegated to {@link GdxTextureStore}.
 *
 * @see GdxTextureStore
 */
@CustomSerializable
final class TextureStore implements ITextureStore {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(TextureStore.class);

    private final StaticRef<GdxTextureStore> gdxTextureStore = StaticEnvironment.TEXTURE_STORE;
    private final ImageResourceLoader resourceLoader;

    private transient TextureCache textureCache;

    public TextureStore(ImageResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

        initTransients();
    }

    private void initTransients() {
        textureCache = new TextureCache(new CacheLoader<ResourceId, ITexture>() {
            @Override
            public ITexture load(ResourceId resourceId) throws Exception {
                return loadTexture(resourceId);
            }
        });
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public void preloadNormalized(ResourceId resourceId) {
        gdxTextureStore.get().preload(resourceLoader.getAbsolutePath(resourceId.getFilePath()));
    }

    @Override
    public void clear() {
        textureCache.clear();
    }

    @Override
    public @Nullable ITexture getTexture(ResourceId id) {
        return textureCache.getTexture(id);
    }

    @Override
    public ITexture getColorTexture(int argb) {
        String filename = ColorTextureLoader.getFilename(argb);
        ResourceId resourceId = new ResourceId(MediaType.IMAGE, FilePath.of(filename));

        ITexture texture = getTexture(resourceId);
        return Checks.checkNotNull(texture, "Color texture loading should never fail");
    }

    /**
     * @return The loaded texture (never {@code null}).
     * @throws IOException If loading the texture failed.
     */
    private ITexture loadTexture(ResourceId resourceId) throws IOException {
        FilePath relPath = resourceId.getFilePath();

        IImageDefinition imageDef = resourceLoader.getImageDef(relPath);
        if (imageDef == null && resourceId.hasSubId()) {
            LOG.warn("Image definition not found: {}", relPath);
            throw new FileNotFoundException("Texture sub-rect not found (missing image definition): " + resourceId);
        }

        FilePath absolutePath = resourceLoader.getAbsolutePath(relPath);
        IResource<Texture> res = gdxTextureStore.get().get(absolutePath);
        if (res == null) {
            throw new FileNotFoundException("Texture resource not found: " + absolutePath);
        }

        double scale = resourceLoader.getImageScale();

        // Create ITexture wrapper for GDX Texture based sub-rect defined by the imageDef
        if (imageDef != null) {
            LOG.trace("Image definition found: {}", relPath);

            if (resourceId.hasSubId()) {
                IImageSubRect subRect = imageDef.findSubRect(resourceId.getSubId());
                if (subRect != null) {
                    LOG.debug("Load image sub-rect: {}: {}", resourceId, subRect.getArea());
                    return new GdxTexture(new SubTextureResource(res, subRect.getArea()), scale, scale);
                } else {
                    LOG.warn("Image definition sub-rect not found: {}", resourceId);
                    throw new FileNotFoundException("Texture sub-rect not found: " + resourceId);
                }
            }
        }
        return new GdxTexture(new SubTextureResource(res, null), scale, scale);
    }

    public static DurationLogger startLoadDurationLogger(Logger logger) {
        DurationLogger dl = DurationLogger.createStarted(logger);
        dl.setInfoLimit(Duration.fromMillis(32)); // 2 frames @ 60Hz
        return dl;
    }

    /**
     * Cache for {@link ITexture} objects. Creating an {@link ITexture} wrapper isn't nearly as expensive as
     * loading the underlying OpenGL Texture, but the cost is still non-trivial and creating a bunch of identical
     * wrappers wastes memory.
     */
    private static final class TextureCache {

        private final LoadingCache<ResourceId, ITexture> cache;

        public TextureCache(CacheLoader<ResourceId, ITexture> loadFunction) {
            cache = CacheBuilder.newBuilder()
                    .concurrencyLevel(1)
                    .weakValues()
                    .build(loadFunction);
        }

        public void clear() {
            cache.invalidateAll();
        }

        /**
         * @return The texture matching the given resource ID, or {@code null} if no such texture exists.
         */
        public @Nullable ITexture getTexture(ResourceId resourceId) {
            try {
                return cache.get(resourceId);
            } catch (ExecutionException e) {
                LOG.warn("Error loading texture ({}): {}", resourceId, e.getCause().toString());
                return null;
            }
        }

    }

    private static final class SubTextureResource extends TransformedResource<Texture, TextureRegion> {

        private static final long serialVersionUID = 1L;

        private final @Nullable Area subRect;

        public SubTextureResource(IResource<Texture> tex, @Nullable Area subRect) {
            super(tex);

            this.subRect = subRect;
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

}
