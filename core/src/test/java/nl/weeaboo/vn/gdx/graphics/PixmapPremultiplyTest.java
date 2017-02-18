package nl.weeaboo.vn.gdx.graphics;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.gdx.HeadlessGdx;

@RunWith(Parameterized.class)
public class PixmapPremultiplyTest {

    static {
        HeadlessGdx.init();
    }

    /**
     * Run this test for every libGDX pixmap format.
     */
    @Parameters(name = "{index}: {0}")
    public static final Format[] params() {
        return Format.values();
    }

    private static final int ORIGINAL_RGBA = 0x22446680;

    private final Format format;

    public PixmapPremultiplyTest(Format format) {
        this.format = format;
    }

    @Test
    public void testPremultiply() {
        Pixmap pixmap = new Pixmap(1, 1, format);
        pixmap.setColor(ORIGINAL_RGBA);
        pixmap.fill();
        PremultUtil.premultiplyAlpha(pixmap);
        int actual = pixmap.getPixel(0, 0);

        // Check that PixmapUtil.premultiplyAlpha() is equivalent to premultiplying every pixel
        Assert.assertEquals(String.format("%08x", getExpectedRGBA()),
                String.format("%08x", actual));

        pixmap.dispose();
    }

    private int getExpectedRGBA() {
        switch (format) {
        case Alpha:
        case Intensity:
            // Pixmap returns 0xffffff for the RGB part of alpha formats
            return 0xffffff80;
        case LuminanceAlpha:
            int intensity = (int)Math.round(0.2126f * 0x11 + 0.7152 * 0x22 + 0.0722 * 0x33);
            Assert.assertEquals(0x20, intensity);
            return 0x20202080;
        case RGB565:
            return 0x204462ff;
        case RGB888:
            return 0x224466ff;
        case RGBA4444:
            // Using RGBA4444 changes the alpha value due to limited precision
            return 0x11223388;
        case RGBA8888:
            return 0x11223380;
        }
        throw new AssertionError("Unsupported format: " + format);
    }

}
