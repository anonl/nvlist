package nl.weeaboo.vn.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ResourceStoreCacheTest {

    private ResourceStoreCacheConfig<Resource> config;
    private TestResourceStoreCache cache;

    @Before
    public void before() {
        config = new ResourceStoreCacheConfig<>();
        config.setWeigher(new DefaultWeigher<>());
        config.setMaximumWeight(3);

        cache = new TestResourceStoreCache(config);

        Assert.assertEquals(config.getMaximumWeight(), cache.getMaximumWeight());
    }

    /**
     * Clearing the cache should clear entries from both the regular cache as well as the preloader cache.
     */
    @Test
    public void testClear() throws ExecutionException {
        cache.get("preload-and-load");

        // TODO: This was removed
        // cache.consumeUnloadCount(1); // Loading an entry invalidates the preloader entry

        cache.get("load");

        // Clearing the cache unloads the 'preload' from the preloader cache, and the two entries from the load cache
        cache.clear();
        cache.consumeUnloadCount(3);
    }

    @Test
    public void testEstimateWeight() throws ExecutionException {
        cache.get("A");
        assertWeight(1);

        // An already cached entry doesn't need to be loaded, so doesn't cause any extra memory use
        cache.get("A");
        assertWeight(1);

        cache.get("B");
        cache.get("C");
        assertWeight(3);

        // Attempting to exceed to maximum weight causes entries to be unloaded
        Assert.assertEquals(3, cache.getMaximumWeight());
        cache.get("D");
        Assert.assertEquals(3, cache.getMaximumWeight());
    }

    private void assertWeight(int expected) {
        Assert.assertEquals(expected, cache.estimateWeight());
    }

    private static final class TestResourceStoreCache extends ResourceStoreCache<String, Resource> {

        private final AtomicInteger unloadCount = new AtomicInteger();

        public TestResourceStoreCache(ResourceStoreCacheConfig<Resource> config) {
            super(config);
        }

        public void consumeUnloadCount(int expected) {
            Assert.assertEquals(expected, unloadCount.getAndSet(0));
        }

        @Override
        protected void doUnload(String resourceKey, Resource value) {
            unloadCount.incrementAndGet();
        }

        @Override
        protected Resource doLoad(String resourceKey) throws Exception {
            return new Resource();
        }

    }

    private static final class Resource {
    }
}
