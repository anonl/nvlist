package nl.weeaboo.gdx.res;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;

public class LoadingResourceStoreTest {

    private LoadingResourceStore<Pixmap> cam;

    @Before
    public void init() {        
        AssetManager assetManager = new TestAssetManager();
        cam = new LoadingResourceStore<Pixmap>(Pixmap.class, assetManager);
    }
    
    @Test
    public void load() {
        IResource<Pixmap> imgResource = cam.get("test.png");
        Assert.assertNotNull(imgResource);
        Pixmap img = imgResource.get();
        Assert.assertNotNull(img);        
        Assert.assertTrue(img.getWidth() > 0);
        Assert.assertTrue(img.getHeight() > 0);
    }
    
    @Test
    public void invalidateCache() {
        IResource<Pixmap> imgResource = cam.get("test.png");
        Assert.assertNotNull(imgResource);
        Pixmap img1 = imgResource.get();
        Assert.assertNotNull(img1);
        
        cam.clear();
        
        // Even after clearing the cache, we can still use the same IResource to load the image
        Pixmap img2 = imgResource.get();
        Assert.assertNotNull(img2);
        Assert.assertNotSame(img1, img2); // Check that img2 is not just a stale reference to a disposed img1
    }
    
}
