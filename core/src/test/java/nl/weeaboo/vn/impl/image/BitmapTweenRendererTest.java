package nl.weeaboo.vn.impl.image;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;

public class BitmapTweenRendererTest {

    private IImageModule imageModule;
    private BitmapTweenConfig defaultConfig;

    @Before
    public void before() {
        HeadlessGdx.init();

        imageModule = new ImageModuleStub();

        TextureMock controlTex = new TextureMock();
        defaultConfig = new BitmapTweenConfig(30, new ControlImage(controlTex, false));
    }

    /** Basic flow for animated renderer: attach, update until finish, detach */
    @Test(timeout = 5000)
    public void basicFlow() {
        BitmapTweenRendererMock btr = new BitmapTweenRendererMock(imageModule, defaultConfig);
        btr.onAttached(null);
        while (!btr.isFinished()) {
            btr.update();
        }
        btr.onDetached(null);
    }

}
