package nl.weeaboo.vn.impl.image;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.io.Resources;

import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.gdx.graphics.PixmapTester;

public class PixmapDecodingScreenshotTest {

    private final PixmapTester pixmapTester = new PixmapTester();

    @Before
    public void before() {
        HeadlessGdx.init();
    }

    @Test
    public void testSaveLoad() throws IOException {
        byte[] inputBytes = Resources.toByteArray(getClass().getResource("/img/test.png"));

        PixmapDecodingScreenshot ss = new PixmapDecodingScreenshot(inputBytes);
        ss = SerializeTester.reserialize(ss);

        Pixmap expectedPixmap = PixmapLoader.load(inputBytes, 0, inputBytes.length);
        pixmapTester.assertEquals(expectedPixmap, ((PixelTextureData)ss.getPixels()).borrowPixels());
    }

    @Test
    public void testCancel() throws IOException {
        byte[] inputBytes = Resources.toByteArray(getClass().getResource("/img/test.png"));

        PixmapDecodingScreenshot ss = new PixmapDecodingScreenshot(inputBytes);
        Assert.assertEquals(true, ss.isAvailable());
        Assert.assertEquals(false, ss.isFailed());
        Assert.assertEquals(false, ss.isTransient());
        Assert.assertEquals(false, ss.isVolatile());

        ss.cancel();

        Assert.assertEquals(false, ss.isAvailable());
        Assert.assertEquals(true, ss.isFailed());
        Assert.assertEquals(false, ss.isTransient());
        Assert.assertEquals(false, ss.isVolatile());
    }

    /**
     * Empty/cancelled screenshots require special handling.
     */
    @Test
    public void testEmpty() {
        PixmapDecodingScreenshot ss = new PixmapDecodingScreenshot(new byte[0]);
        ss.cancel();

        Assert.assertEquals(false, ss.isAvailable());
        Assert.assertEquals(true, ss.isFailed());
        Assert.assertEquals(false, ss.isTransient());
        Assert.assertEquals(false, ss.isVolatile());

        ss = SerializeTester.reserialize(ss);

        Assert.assertEquals(null, ss.getPixels());
    }

}
