package nl.weeaboo.vn.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;

/**
 * In-memory resource cache.
 */
public abstract class ResourceStoreCache<K, V> {

    private final int maximumWeight;
    private final IWeigher<? super V> weigher;

    private final LoadingCache<K, V> cache;

    protected ResourceStoreCache(ResourceStoreCacheConfig<? super V> config) {
        maximumWeight = config.getMaximumWeight();
        weigher = config.getWeigher();

        cache = buildLoadCache();
    }

    /**
     * Returns an estimate of the total weight of all loaded entries.
     */
    public long estimateWeight() {
        long sum = 0L;
        for (V value : cache.asMap().values()) {
            sum += weigher.weigh(value);
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

    private LoadingCache<K, V> buildLoadCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .maximumWeight(maximumWeight)
                .removalListener(new RemovalListener<K, V>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, V> notification) {
                        doUnload(notification.getKey(), notification.getValue());
                    }
                })
                .weigher(new Weigher<K, V>() {
                    @Override
                    public int weigh(K key, V value) {
                        return weigher.weigh(value);
                    }
                })
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K resourceKey) throws Exception {
                        return doLoad(resourceKey);
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
     * Gets an entry from the cache, loading it if needed.
     *
     * @throws ExecutionException If a checked exception was thrown while loading the value.
     */
    public V get(K resourceKey) throws ExecutionException {
        return cache.get(resourceKey);
    }

    /**
     * Clears the cache, invalidating all previously loaded entries.
     */
    public void clear() {
        cache.invalidateAll();
    }

}
