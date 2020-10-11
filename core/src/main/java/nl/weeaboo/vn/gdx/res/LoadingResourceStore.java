package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.Duration;
import nl.weeaboo.vn.impl.core.DurationLogger;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Resource stores which loads pre-existing resources using an {@link AssetManager}.
 */
public class LoadingResourceStore<T> extends ResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(LoadingResourceStore.class);

    private final StaticRef<? extends LoadingResourceStore<T>> selfId;
    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;
    private final Class<T> assetType;

    private LoadingResourceStoreCache cache;

    public LoadingResourceStore(StaticRef<? extends LoadingResourceStore<T>> selfId, Class<T> type) {
        super(LoggerFactory.getLogger("LoadingResourceStore<" + type.getSimpleName() + ">"));

        this.selfId = selfId;
        this.assetType = Checks.checkNotNull(type);

        cache = new LoadingResourceStoreCache(new ResourceStoreCacheConfig<>());
    }

    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * Request a preload of the given resource.
     */
    public void preload(FilePath absolutePath) {
        startLoading(absolutePath, "preload");
    }

    public static DurationLogger startLoadDurationLogger(Logger logger) {
        DurationLogger dl = DurationLogger.createStarted(logger);
        dl.setInfoLimit(Duration.fromMillis(32)); // 2 frames @ 60Hz
        return dl;
    }

    protected T loadResource(FilePath absolutePath) {
        DurationLogger dl = startLoadDurationLogger(LOG);

        AssetManager am = assetManager.get();

        // Finish loading resource
        String pathString = absolutePath.toString();
        boolean alreadyLoaded = am.isLoaded(pathString);
        if (!alreadyLoaded) {
            startLoading(absolutePath, "Start loading");
            am.finishLoadingAsset(pathString);
        }
        T resource = am.get(pathString);

        if (alreadyLoaded) {
            dl.logDuration("Loading resource (preloaded) '{}'", absolutePath);
        } else {
            dl.logDuration("Loading resource (cache miss) '{}'", absolutePath);
        }

        return resource;
    }

    private void startLoading(FilePath absolutePath, String message) {
        AssetManager am = assetManager.get();
        String pathString = absolutePath.toString();

        if (!am.isLoaded(pathString)) {
            LOG.debug("{}: {}", message, absolutePath);

            am.load(pathString, assetType, getLoadParams(absolutePath));
        }
    }

    /**
     * @param absolutePath The path to the resource that's being loaded (may not exist).
     */
    @Nullable
    protected AssetLoaderParameters<T> getLoadParams(FilePath absolutePath) {
        return null;
    }

    /**
     * Attempts to load the resource with the given name.
     *
     * @return A resource wrapper pointing to the resource, or {@code null} if the resource couldn't be loaded.
     */
    public @Nullable IResource<T> get(FilePath absolutePath) {
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

    protected final ResourceStoreCache<FilePath, ? extends IResource<T>> getCache() {
        return cache;
    }

    private final class LoadingResourceStoreCache extends ResourceStoreCache<FilePath, FileResource<T>> {

        public LoadingResourceStoreCache(ResourceStoreCacheConfig<T> config) {
            super(config.map(w -> new ResourceWeigher<>(w)));
        }

        @Override
        public FileResource<T> doLoad(FilePath absolutePath) throws IOException {
            try {
                return new FileResource<>(selfId, absolutePath, loadResource(absolutePath));
            } catch (RuntimeException re) {
                loadError(absolutePath, re);
                throw new IOException("Error loading file: " + absolutePath, re);
            }
        }

        @Override
        protected void doUnload(FilePath absolutePath, FileResource<T> value) {
            AssetManager am = assetManager.get();

            String pathString = absolutePath.toString();
            if (am.isLoaded(pathString)) {
                LOG.debug("Unloading resource: {}", pathString);
                value.invalidate();
                am.unload(pathString);
            }
        }
    }

}
