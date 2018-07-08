package nl.weeaboo.vn.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;

import nl.weeaboo.filesystem.FilePath;

public abstract class ResourceStoreCache<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceStoreCache.class);

    private final int maximumWeight;
    private final IWeigher<T> weigher;

    private final LoadingCache<FilePath, PreloadRef> preloadCache;
    private final LoadingCache<FilePath, Ref<T>> cache;

    public ResourceStoreCache(ResourceStoreCacheConfig<T> config) {
        maximumWeight = config.getMaximumWeight();
        weigher = config.getWeigher();

        preloadCache = buildPreloadCache();
        cache = buildLoadCache();
    }

    /**
     * Returns an estimate of the total weight of all loaded entries.
     */
    public long estimateWeight() {
        long sum = 0L;
        for (Ref<T> ref : cache.asMap().values()) {
            T value = ref.get();
            if (value != null) {
                sum += weigher.weigh(value);
            }
        }
        return sum;
    }

    /**
     * Returns the maximum size of the cache (in 'weight').
     *
     * @see #estimateWeight()
     */
    public long getMaximumWeight() {
        return maximumWeight;
    }

    private LoadingCache<FilePath, PreloadRef> buildPreloadCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(15, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<FilePath, PreloadRef>() {
                    @Override
                    public void onRemoval(RemovalNotification<FilePath, PreloadRef> notification) {
                        doUnload(notification.getKey());
                    }
                })
                .build(new CacheLoader<FilePath, PreloadRef>() {
                    @Override
                    public PreloadRef load(FilePath absolutePath) {
                        doPreload(absolutePath);
                        return new PreloadRef();
                    }
                });
    }

    private LoadingCache<FilePath, Ref<T>> buildLoadCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .weigher(new RefWeigher<>(weigher))
                .maximumWeight(maximumWeight)
                .removalListener(new RemovalListener<FilePath, Ref<T>>() {
                    @Override
                    public void onRemoval(RemovalNotification<FilePath, Ref<T>> notification) {
                        notification.getValue().invalidate();
                        doUnload(notification.getKey());
                    }
                })
                .build(new CacheLoader<FilePath, Ref<T>>() {
                    @Override
                    public Ref<T> load(FilePath absolutePath) {
                        return doLoad(absolutePath);
                    }
                });
    }

    /**
     * Disposes/unloads the resource with the given path.
     */
    protected abstract void doUnload(FilePath absolutePath);

    /**
     * Creates the resource with the given path.
     */
    protected abstract Ref<T> doLoad(FilePath absolutePath);

    /**
     * Implements async preloading for the resource with the given path.
     *
     * @param absolutePath The file path correspsonding to the resource.
     */
    protected void doPreload(FilePath absolutePath) {
    }

    protected Ref<T> getEntry(FilePath absolutePath) throws ExecutionException {
        return cache.get(absolutePath);
    }

    /**
     * Triggers an async preload of the given resource.
     *
     * @see #doPreload(FilePath)
     */
    public void preload(FilePath absolutePath) {
        try {
            preloadCache.get(absolutePath);
        } catch (ExecutionException e) {
            LOG.warn("Preload failed: {}", absolutePath, e.getCause());
        }
    }

    /**
     * Clears the cache, invalidating all previously loaded entries.
     */
    public void clear() {
        preloadCache.invalidateAll();
        cache.invalidateAll();
    }

    private static final class PreloadRef {
    }

    private static final class RefWeigher<T> implements Weigher<FilePath, Ref<T>> {

        private final IWeigher<T> weigher;

        public RefWeigher(IWeigher<T> weigher) {
            this.weigher = weigher;
        }

        @Override
        public int weigh(FilePath key, Ref<T> ref) {
            final T value = ref.get();
            if (value == null) {
                return 0;
            }
            return weigher.weigh(value);
        }
    }
}
