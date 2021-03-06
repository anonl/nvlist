package nl.weeaboo.vn.gdx.res;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

public class LoadingResourceStoreTest {

    private static final FilePath TEST_FILENAME = FilePath.of("img/test.png");
    private static final FilePath ERROR_FILENAME = FilePath.of("img/invalid.png");

    private StaticRef<PixmapResourceStore> testId = StaticRef.from("test", PixmapResourceStore.class);
    private AssetManagerMock assetManager;
    private PixmapResourceStore cam;

    @Before
    public void init() {
        assetManager = new AssetManagerMock();
        StaticEnvironment.ASSET_MANAGER.set(assetManager);
        testId.set(cam = new PixmapResourceStore(testId));
    }

    @After
    public void deinit() {
        cam.clear();
    }

    @Test
    public void testLoad() {
        IResource<Pixmap> imgResource = cam.getResource(TEST_FILENAME);
        Assert.assertNotNull(imgResource);

        Pixmap img = imgResource.get();
        Assert.assertNotNull(img);
        Assert.assertTrue(img.getWidth() > 0);
        Assert.assertTrue(img.getHeight() > 0);
    }

    @Test
    public void testLoadException() {
        cam.consumeLoadErrorCount(0);

        Assert.assertNull(cam.getResource(ERROR_FILENAME));
        cam.consumeLoadErrorCount(1);

        // A LRU set is used to throttle load errors. Resource loads throwing an exception aren't retried.
        Assert.assertNull(cam.getResource(ERROR_FILENAME));
        cam.consumeLoadErrorCount(0); // No load error
    }

    @Test
    public void testPreload() {
        cam.preload(TEST_FILENAME);

        // Wait for the image to finish loading
        assetManager.finishLoading();

        Assert.assertEquals(true, assetManager.isLoaded(TEST_FILENAME.toString()));
    }

    /**
     * The cache config can be changed at runtime. As a side-effect, the entire cache is cleared.
     */
    @Test
    public void testRuntimeReconfigure() {
        Assert.assertEquals(20, cam.getCache().getMaximumWeight());

        ResourceStoreCacheConfig<Pixmap> newConfig = new ResourceStoreCacheConfig<>();
        newConfig.setMaximumWeight(3);
        cam.setCacheConfig(newConfig);

        Assert.assertEquals(3, cam.getCache().getMaximumWeight());
    }

    @Test
    public void testInvalidateCache() {
        IResource<Pixmap> imgResource = cam.getResource(TEST_FILENAME);
        Assert.assertNotNull(imgResource);
        Pixmap img1 = imgResource.get();
        Assert.assertNotNull(img1);

        cam.clear();

        // Even after clearing the cache, we can still use the same IResource to load the image
        Pixmap img2 = imgResource.get();
        Assert.assertNotNull(img2);
        Assert.assertNotSame(img1, img2); // Check that img2 is not just a stale reference to a disposed img1
    }

    private static class PixmapResourceStore extends LoadingResourceStore<Pixmap> {

        private final AtomicInteger loadErrorCount = new AtomicInteger();

        public PixmapResourceStore(StaticRef<PixmapResourceStore> selfId) {
            super(selfId, Pixmap.class);
        }

        @Override
        protected Pixmap loadResource(FilePath absolutePath) {
            if (Objects.equals(absolutePath, ERROR_FILENAME)) {
                throw new GdxRuntimeException("test");
            }

            return super.loadResource(absolutePath);
        }

        @Override
        protected void loadError(FilePath path, Throwable cause) {
            loadErrorCount.incrementAndGet();

            super.loadError(path, cause);
        }

        void consumeLoadErrorCount(int expected) {
            Assert.assertEquals(expected, loadErrorCount.getAndSet(0));
        }
    }

}
