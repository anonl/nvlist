package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.scene.ILayer;

public class ScreenTest {

    @Test
    public void layers() {
        Screen screen = CoreTestUtil.newScreen();
        ILayer active = screen.getActiveLayer();
        Assert.assertNotNull(active); // Active layer should never be null
        ILayer root = screen.getRootLayer();
        Assert.assertSame(active, root); // Root layer should never be null

        // Sub-layer creation and containsLayer() test
        ILayer subLayer = screen.createLayer(root);
        ILayer subSubLayer = screen.createLayer(subLayer);
        Assert.assertTrue(root.containsLayer(subLayer));
        Assert.assertTrue(root.containsLayer(subSubLayer));
        Assert.assertTrue(subLayer.containsLayer(subSubLayer));
    }

}
