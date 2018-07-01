package nl.weeaboo.vn.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.DurationLogger;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

public class LoadingResourceStore<T> extends AbstractResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(LoadingResourceStore.class);

    private final StaticRef<? extends LoadingResourceStore<T>> selfId;
    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;
    private final Class<T> assetType;

    private LoadingCache<FilePath, PreloadRef> preloadCache;
    private LoadingCache<FilePath, Ref<T>> cache;

    public LoadingResourceStore(StaticRef<? extends LoadingResourceStore<T>> selfId, Class<T> type) {
        super(LoggerFactory.getLogger("LoadingResourceStore<" + type.getSimpleName() + ">"));

        this.selfId = selfId;
        this.assetType = Checks.checkNotNull(type);

        preloadCache = buildPreloadCache();
        cache = buildLoadCache();
    }

    private LoadingCache<FilePath, PreloadRef> buildPreloadCache() {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(15, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<FilePath, PreloadRef>() {
                    @Override
                    public void onRemoval(RemovalNotification<FilePath, PreloadRef> notification) {
                        unloadResource(notification.getKey());
                    }
                })
                .build(new PreloadFunction());
    }

    private LoadingCache<FilePath, Ref<T>> buildLoadCache() {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(15, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<FilePath, Ref<T>>() {
                    @Override
                    public void onRemoval(RemovalNotification<FilePath, Ref<T>> notification) {
                        notification.getValue().invalidate();
                        unloadResource(notification.getKey());
                    }
                })
                .build(new LoadFunction());
    }

    @Override
    public void clear() {
        preloadCache.invalidateAll();
        cache.invalidateAll();
    }

    /**
     * Request a preload of the given resource.
     */
    public void preload(FilePath absolutePath) {
        try {
            preloadCache.get(absolutePath);
        } catch (ExecutionException e) {
            LOG.warn("Preload failed: {}", absolutePath, e.getCause());
        }
    }

    protected T loadResource(FilePath absolutePath) {
        DurationLogger dl = DurationLogger.createStarted(LOG);
        AssetManager am = assetManager.get();

        // Finish loading resource
        String pathString = absolutePath.toString();
        startLoading(absolutePath);
        am.finishLoadingAsset(pathString);
        T resource = am.get(pathString);

        dl.logDuration("Loading resource '{}'", absolutePath);

        return resource;
    }

    private void startLoading(FilePath absolutePath) {
        AssetManager am = assetManager.get();
        String pathString = absolutePath.toString();

        LOG.debug("Start loading: {}", absolutePath);

        am.load(pathString, assetType, getLoadParams(absolutePath));
    }

    /**
     * @param absolutePath The path to the resource that's being loaded (may not exist).
     */
    @Nullable
    protected AssetLoaderParameters<T> getLoadParams(FilePath absolutePath) {
        return null;
    }

    private void unloadResource(FilePath absolutePath) {
        AssetManager am = assetManager.get();
        am.unload(absolutePath.toString());
    }

    /**
     * Attempts to load the resource with the given name.
     *
     * @return A resource wrapper pointing to the resource, or {@code null} if the resource couldn't be loaded.
     */
    public @Nullable IResource<T> get(FilePath absolutePath) {
        Ref<T> entry = getEntry(absolutePath);
        if (entry == null) {
            return null;
        }

        FileResource<T> resource = new FileResource<>(selfId, absolutePath);
        resource.set(entry);
        return resource;
    }

    protected @Nullable Ref<T> getEntry(FilePath absolutePath) {
        try {
            return cache.get(absolutePath);
        } catch (ExecutionException e) {
            loadError(absolutePath, e.getCause());
            return null;
        }
    }

    private class LoadFunction extends CacheLoader<FilePath, Ref<T>> {

        @Override
        public Ref<T> load(FilePath absolutePath) {
            T resource;
            try {
                resource = loadResource(absolutePath);
            } catch (RuntimeException re) {
                loadError(absolutePath, re);
                resource = null;
            }
            return new Ref<>(resource);
        }

    }

    private class PreloadFunction extends CacheLoader<FilePath, PreloadRef> {

        @Override
        public PreloadRef load(FilePath absolutePath) {
            startLoading(absolutePath);
            return new PreloadRef();
        }

    }

    private static final class PreloadRef {
    }

}
