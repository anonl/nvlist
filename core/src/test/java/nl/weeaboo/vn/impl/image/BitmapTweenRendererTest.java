package nl.weeaboo.vn.impl.image;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;

public class BitmapTweenRendererTest {

    private IImageModule imageModule;
    private BitmapTweenConfig defaultConfig;

    @Before
    public void before() {
        imageModule = new ImageModuleStub();

        TestTexture controlTex = new TestTexture();
        defaultConfig = new BitmapTweenConfig(30, new ControlImage(controlTex, false));
    }

    /** Basic flow for animated renderer: attach, update until finish, detach */
    @Test(timeout = 5000)
    public void basicFlow() {
        TestBitmapTweenRenderer btr = new TestBitmapTweenRenderer(imageModule, defaultConfig);
        btr.onAttached(null);
        while (!btr.isFinished()) {
            btr.update();
        }
        btr.onDetached(null);
    }

}
