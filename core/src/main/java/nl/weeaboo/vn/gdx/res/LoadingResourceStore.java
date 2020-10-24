package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Caching version of {@link AssetManager}.
 */
public class LoadingResourceStore<T> extends AssetManagerResourceStore<T> {

    private static final Logger LOG = LoggerFactory.getLogger(LoadingResourceStore.class);

    private final StaticRef<? extends LoadingResourceStore<T>> selfId;

    private LoadingResourceStoreCache cache;

    public LoadingResourceStore(StaticRef<? extends LoadingResourceStore<T>> selfId, Class<T> type) {
        super(type);

        this.selfId = selfId;

        cache = new LoadingResourceStoreCache(new ResourceStoreCacheConfig<>());
    }

    @Override
    public void clear() {
        cache.clear();

        super.clear();
    }

    /**
     * Attempts to load the resource with the given name.
     *
     * @return A resource wrapper pointing to the resource, or {@code null} if the resource couldn't be loaded.
     */
    public @Nullable IResource<T> getResource(FilePath absolutePath) {
        Ref<T> valueRef = getValueRef(absolutePath);
        if (valueRef == null) {
            return null; // Load error
        }
        return new FileResource<>(selfId, absolutePath, valueRef);
    }

    @Nullable Ref<T> getValueRef(FilePath absolutePath) {
        if (getLoadState(absolutePath) == ELoadState.ERROR) {
            // Don't attempt to (re)load resources that we know are broken
            return null;
        }

        try {
            return cache.get(absolutePath);
        } catch (ExecutionException e) {
            loadError(absolutePath, e.getCause());
            return null;
        }
    }

    /**
     * Changes the cache config (invalidates all existing cached entries).
     */
    public final void setCacheConfig(ResourceStoreCacheConfig<T> config) {
        // Clear existing cache
        cache.clear();

        LOG.info("{}.setCacheConfig(maxWeight={})", getClass().getSimpleName(), config.getMaximumWeight());

        // Init a new cache with the new config
        cache = new LoadingResourceStoreCache(config);
    }

    protected final ResourceStoreCache<FilePath, ?> getCache() {
        return cache;
    }

    private final class LoadingResourceStoreCache extends ResourceStoreCache<FilePath, Ref<T>> {

        public LoadingResourceStoreCache(ResourceStoreCacheConfig<T> config) {
            super(config.map(w -> new RefWeigher<>(w)));
        }

        @Override
        public Ref<T> doLoad(FilePath absolutePath) throws IOException {
            try {
                return new Ref<>(loadResource(absolutePath));
            } catch (RuntimeException re) {
                throw new IOException("Error loading file: " + absolutePath, re);
            }
        }

        @Override
        protected void doUnload(FilePath absolutePath, Ref<T> ref) {
            ref.invalidate();
            unloadResource(absolutePath);
        }

    }



}
