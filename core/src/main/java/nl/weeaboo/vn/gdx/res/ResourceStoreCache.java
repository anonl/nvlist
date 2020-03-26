package nl.weeaboo.vn.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;

/**
 * In-memory cache of a {@link LoadingResourceStore}.
 */
public abstract class ResourceStoreCache<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceStoreCache.class);

    private final int maximumWeight;
    private final IWeigher<V> weigher;

    private final LoadingCache<K, PreloadRef> preloadCache;
    private final LoadingCache<K, Ref<V>> cache;

    public ResourceStoreCache(ResourceStoreCacheConfig<V> config) {
        maximumWeight = config.getMaximumWeight();
        weigher = config.getWeigher();

        cache = buildLoadCache();
        preloadCache = buildPreloadCache();
    }

    /**
     * Returns an estimate of the total weight of all loaded entries.
     */
    public long estimateWeight() {
        long sum = 0L;
        for (Ref<V> ref : cache.asMap().values()) {
            V value = ref.get();
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

    private LoadingCache<K, PreloadRef> buildPreloadCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(15, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<K, PreloadRef>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, PreloadRef> notification) {
                        K key = notification.getKey();

                        if (cache.getIfPresent(key) == null) {
                            // Unload resource if it was contained only in the preload cache
                            doUnload(notification.getKey(), null);
                        }
                    }
                })
                .build(new CacheLoader<K, PreloadRef>() {
                    @Override
                    public PreloadRef load(K resourceKey) throws Exception {
                        doPreload(resourceKey);
                        return new PreloadRef();
                    }
                });
    }

    private LoadingCache<K, Ref<V>> buildLoadCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .weigher(new RefWeigher<>(weigher))
                .maximumWeight(maximumWeight)
                .removalListener(new RemovalListener<K, Ref<V>>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, Ref<V>> notification) {
                        Ref<V> ref = notification.getValue();
                        V value = ref.get();
                        ref.invalidate();

                        doUnload(notification.getKey(), value);
                    }
                })
                .build(new CacheLoader<K, Ref<V>>() {
                    @Override
                    public Ref<V> load(K resourceKey) throws Exception {
                        V resource = doLoad(resourceKey);

                        // Remove resource from the preload cache because it's now fully loaded
                        preloadCache.invalidate(resourceKey);

                        return new Ref<>(resource);
                    }
                });
    }

    /**
     * Disposes/unloads the resource with the given key.
     */
    protected abstract void doUnload(K resourceKey, @Nullable V value);

    /**
     * Creates the resource with the given key.
     */
    protected abstract V doLoad(K resourceKey) throws Exception;

    /**
     * Implements async preloading for the resource with the given key.
     *
     * @param resourceKey The unique identifier correspsonding to the resource.
     */
    protected void doPreload(K resourceKey) throws Exception {
    }

    /**
     * Gets an entry from the cache, loading it if needed.
     *
     * @throws ExecutionException If a checked exception was thrown while loading the value.
     */
    public Ref<V> getEntry(K resourceKey) throws ExecutionException {
        return cache.get(resourceKey);
    }

    /**
     * Triggers an async preload of the given resource.
     *
     * @see #doPreload(Object)
     */
    public void preload(K resourceKey) {
        try {
            preloadCache.get(resourceKey);
        } catch (ExecutionException e) {
            LOG.warn("Preload failed: {}", resourceKey, e.getCause());
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

    private static final class RefWeigher<K, V> implements Weigher<K, Ref<V>> {

        private final IWeigher<V> weigher;

        public RefWeigher(IWeigher<V> weigher) {
            this.weigher = weigher;
        }

        @Override
        public int weigh(K key, Ref<V> ref) {
            final V value = ref.get();
            if (value == null) {
                return 0;
            }
            return weigher.weigh(value);
        }
    }
}
