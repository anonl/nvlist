package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.impl.core.SkipState;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.scene.ILayer;

public class ScreenTest {

    private TestEnvironment env;
    private SkipState skipState;
    private Screen screen;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();
        skipState = new SkipState();
        screen = CoreTestUtil.newScreen(skipState);
    }

    @Test
    public void layers() {
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

    @Test
    public void testAutoReadCancel() {
        skipState.setSkipMode(SkipMode.AUTO_READ);
        Assert.assertEquals(SkipMode.AUTO_READ, skipState.getSkipMode());

        // Pressing the text continue key cancels auto read mode
        env.getInput().buttonPressed(VKey.TEXT_CONTINUE);
        screen.update();

        Assert.assertEquals(SkipMode.NONE, skipState.getSkipMode());
    }

}
