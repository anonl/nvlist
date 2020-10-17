package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
import nl.weeaboo.vn.impl.core.LruSet;
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

    private final Set<FilePath> loading = new HashSet<>();
    private final LruSet<FilePath> invalid = new LruSet<>(128);
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
        invalid.clear();
    }

    /**
     * Request a preload of the given resource.
     */
    public void preload(FilePath absolutePath) {
        ELoadState loadState = getLoadState(absolutePath);
        if (loadState == ELoadState.ERROR) {
            LOG.debug("Blocked preload of broken resource: {}", absolutePath);
        } else if (loadState == ELoadState.UNLOADED) {
            startLoading(absolutePath, "preload");
        }
    }

    /**
     * Returns the current loading state of the resource with the given name.
     */
    protected final ELoadState getLoadState(FilePath absolutePath) {
        AssetManager am = assetManager.get();
        if (am.isLoaded(absolutePath.toString())) {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.LOADED;
        } else if (invalid.contains(absolutePath)) {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.UNLOADED;
        } else if (loading.contains(absolutePath)) {
            return ELoadState.PRELOADING;
        } else {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.UNLOADED;
        }
    }

    private void startLoading(FilePath absolutePath, String message) {
        LOG.debug("{}: {}", message, absolutePath);

        loading.add(absolutePath);

        AssetManager am = assetManager.get();
        am.load(absolutePath.toString(), assetType, getLoadParams(absolutePath));
    }

    public static DurationLogger startLoadDurationLogger(Logger logger) {
        DurationLogger dl = DurationLogger.createStarted(logger);
        dl.setInfoLimit(Duration.fromMillis(32)); // 2 frames @ 60Hz
        return dl;
    }

    /** Loads the resource directly, skipping the resource cache. */
    protected T loadResource(FilePath absolutePath) {
        DurationLogger dl = startLoadDurationLogger(LOG);

        ELoadState loadState = getLoadState(absolutePath);
        if (loadState == ELoadState.ERROR) {
            throw new IllegalStateException("Refusing to load broken resource " + absolutePath);
        } else if (loadState == ELoadState.UNLOADED) {
            startLoading(absolutePath, "Start loading");
        }

        // Finish loading resource
        AssetManager am = assetManager.get();
        String pathString = absolutePath.toString();
        am.finishLoadingAsset(pathString);
        loading.remove(absolutePath);

        T resource = am.get(pathString);

        invalid.remove(absolutePath);

        if (loadState == ELoadState.LOADED) {
            dl.logDuration("Loading resource (preloaded) '{}'", absolutePath);
        } else {
            dl.logDuration("Loading resource (cache miss) '{}'", absolutePath);
        }

        return resource;
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
    public @Nullable IResource<T> getResource(FilePath absolutePath) {
        Ref<T> valueRef = getValueRef(absolutePath);
        if (valueRef == null) {
            return null; // Load error
        }
        return new FileResource<>(selfId, absolutePath, valueRef);
    }

    @Nullable Ref<T> getValueRef(FilePath absolutePath) {
        try {
            return cache.get(absolutePath);
        } catch (ExecutionException e) {
            // Log load error cause once per resource
            if (invalid.add(absolutePath)) {
                loadError(absolutePath, e.getCause());
            }
            return null;
        }
    }

    @Override
    protected void loadError(FilePath path, Throwable cause) {
        invalid.add(path);
        super.loadError(path, cause);
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

    private enum ELoadState {
        UNLOADED, PRELOADING, LOADED, ERROR;
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
            AssetManager am = assetManager.get();

            String pathString = absolutePath.toString();
            if (am.isLoaded(pathString)) {
                LOG.debug("Unloading resource: {}", pathString);
                ref.invalidate();
                am.unload(pathString);
            }
        }
    }



}
