package nl.weeaboo.vn.impl.image;

import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.image.ITexture;

/**
 * Cache for {@link ITexture} objects. Creating an {@link ITexture} wrapper isn't nearly as expensive as
 * loading the underlying OpenGL Texture, but the cost is still non-trivial and creating a bunch of identical
 * wrappers wastes memory.
 */
final class TextureCache {

    private static final Logger LOG = LoggerFactory.getLogger(TextureCache.class);

    private final LoadingCache<ResourceId, ITexture> cache;

    public TextureCache(CacheLoader<ResourceId, ITexture> loadFunction) {
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .weakValues()
                .build(loadFunction);
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
