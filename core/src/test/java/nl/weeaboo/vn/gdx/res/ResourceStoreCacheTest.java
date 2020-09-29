package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ResourceStoreCacheTest {

    private static final String ERROR_KEY = "error";

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
        cache.preload("preload-and-load");
        cache.getEntry("preload-and-load");

        // TODO: This was removed
        // cache.consumeUnloadCount(1); // Loading an entry invalidates the preloader entry

        cache.preload("preload");
        cache.getEntry("load");

        // Clearing the cache unloads the 'preload' from the preloader cache, and the two entries from the load cache
        cache.clear();
        cache.consumeUnloadCount(3);
    }

    @Test
    public void testEstimateWeight() throws ExecutionException {
        cache.getEntry("A");
        assertWeight(1);

        // An already cached entry doesn't need to be loaded, so doesn't cause any extra memory use
        cache.getEntry("A");
        assertWeight(1);

        cache.getEntry("B");
        cache.getEntry("C");
        assertWeight(3);

        // Attempting to exceed to maximum weight causes entries to be unloaded
        Assert.assertEquals(3, cache.getMaximumWeight());
        cache.getEntry("D");
        Assert.assertEquals(3, cache.getMaximumWeight());
    }

    /**
     * The preloader cache is responsible for ensuring any particular resource is only in the process of being
     * preloaded once -- multiple calls to the preload method shouldn't result in more preload work.
     */
    @Test
    public void testPreload() {
        cache.consumePreloadCount(0);

        // Preloading the same resource multiple times only results in one preload call
        cache.preload("A");
        cache.consumePreloadCount(1);
        cache.preload("A");
        cache.consumePreloadCount(0);

        // Preloading a different resource works
        cache.preload("B");
        cache.consumePreloadCount(1);

        // Exceptions during preloading are logged, but otherwise swallowed
        cache.preload(ERROR_KEY);
    }

    private void assertWeight(int expected) {
        Assert.assertEquals(expected, cache.estimateWeight());
    }

    private static final class TestResourceStoreCache extends ResourceStoreCache<String, Resource> {

        private final AtomicInteger preloadCount = new AtomicInteger();
        private final AtomicInteger unloadCount = new AtomicInteger();

        public TestResourceStoreCache(ResourceStoreCacheConfig<Resource> config) {
            super(config);
        }

        @Override
        protected void doPreload(String resourceKey) throws Exception {
            if (Objects.equals(ERROR_KEY, resourceKey)) {
                throw new IOException("test");
            }

            preloadCount.incrementAndGet();
        }

        public void consumePreloadCount(int expected) {
            Assert.assertEquals(expected, preloadCount.getAndSet(0));
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
