package nl.weeaboo.vn.gdx.res;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

public class LoadingResourceStoreTest {

    private static final FilePath TEST_FILENAME = FilePath.of("img/test.png");
    private StaticRef<PixmapResourceStore> testId = StaticRef.from("test", PixmapResourceStore.class);
    private PixmapResourceStore cam;

    @Before
    public void init() {
        StaticEnvironment.ASSET_MANAGER.set(new TestAssetManager());
        testId.set(cam = new PixmapResourceStore(testId));
    }

    @Test
    public void load() {
        IResource<Pixmap> imgResource = cam.get(TEST_FILENAME);
        Assert.assertNotNull(imgResource);
        Pixmap img = imgResource.get();
        Assert.assertNotNull(img);
        Assert.assertTrue(img.getWidth() > 0);
        Assert.assertTrue(img.getHeight() > 0);
    }

    @Test
    public void invalidateCache() {
        IResource<Pixmap> imgResource = cam.get(TEST_FILENAME);
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

        public PixmapResourceStore(StaticRef<PixmapResourceStore> selfId) {
            super(selfId, Pixmap.class);
        }

    }

}
