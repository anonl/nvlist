package nl.weeaboo.vn.impl.scene;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.image.NinePatchAssert;
import nl.weeaboo.vn.impl.scene.Button;
import nl.weeaboo.vn.impl.scene.ButtonImageLoader;
import nl.weeaboo.vn.impl.scene.ButtonModel;
import nl.weeaboo.vn.impl.script.ScriptEventDispatcher;
import nl.weeaboo.vn.scene.ButtonViewState;

public class ButtonImageLoaderTest {

    private TestEnvironment env;
    private ButtonImageLoader imageLoader;

    private ButtonRendererMock renderer;
    private Button button;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();

        imageLoader = new ButtonImageLoader(env.getImageModule());
        renderer = new ButtonRendererMock();
        button = new Button(new ScriptEventDispatcher(), new ButtonModel(), renderer);
    }

    @After
    public void after() {
        env.destroy();
    }

    /** Textures for each button state are stored in separate images */
    @Test
    public void loadSeparateImages() {
        imageLoader.loadImages(button, loadInfo("button/separate"));

        // Check that the expected textures were loaded
        assertTexture(ButtonViewState.DEFAULT, "button/separate");
        assertTexture(ButtonViewState.ROLLOVER, "button/separate-rollover");
        assertTexture(ButtonViewState.PRESSED, "button/separate-pressed");
        assertTexture(ButtonViewState.DISABLED, "button/separate-disabled");
    }

    /** Textures for each button state are stored in sub-rectangles of a single image */
    @Test
    public void loadSubRects() {
        imageLoader.loadImages(button, loadInfo("button/button"));

        // Check that the expected textures were loaded
        assertNinePatch(ButtonViewState.DEFAULT, "button/button");
        assertNinePatch(ButtonViewState.ROLLOVER, "button/button#rollover");
        assertNinePatch(ButtonViewState.PRESSED, "button/button#pressed");
        assertNinePatch(ButtonViewState.DISABLED, "button/button#disabled");
    }

    private void assertTexture(ButtonViewState viewState, String expectedTexture) {
        ITexture expected = env.getImageModule().getTexture(FilePath.of(expectedTexture));
        Assert.assertNotNull(expected);

        ITexture actual = renderer.getRegularTexture(viewState);
        Assert.assertSame(expected, actual);
    }

    private void assertNinePatch(ButtonViewState viewState, String expectedNinePatch) {
        ResourceLoadInfo loadInfo = loadInfo(expectedNinePatch);
        INinePatch expected = env.getImageModule().getNinePatch(loadInfo, false);
        Assert.assertNotNull(expected);

        INinePatch actual = renderer.getNinePatchTexture(viewState);
        NinePatchAssert.assertEquals(expected, actual);
    }

    private ResourceLoadInfo loadInfo(String path) {
        return new ResourceLoadInfo(FilePath.of(path));
    }

}
