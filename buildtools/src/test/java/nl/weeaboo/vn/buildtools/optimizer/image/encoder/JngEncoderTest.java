package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.optimizer.ImageWithDefTester;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.gdx.graphics.PixmapTester;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

public final class JngEncoderTest {

    private PixmapTester pixmapTester;
    private JngEncoder encoder;

    @Before
    public void before() {
        HeadlessGdx.init();
        pixmapTester = new PixmapTester();
        encoder = new JngEncoder();
    }

    @After
    public void after() {
        pixmapTester.dispose();
    }

    /** Encode an image without alpha. */
    @Test
    public void testEncodeNoAlpha() throws IOException {
        EncodedImage encoded = encode(pixmapTester.newPixmap(Format.RGBA8888, Color.WHITE));

        /*
         * Encoded image has no alpha, because the source image has no alpha. Note that the source format
         * does support alpha, but since no pixels use it, the encoded image doesn't need it.
         */
        Assert.assertEquals(false, encoded.hasAlpha());
    }

    /** Encode an image with color and alpha. */
    @Test
    public void testEncodeWithAlpha() throws IOException {
        EncodedImage encoded = encode(pixmapTester.newPixmap(Format.RGBA8888, new Color(0xFF00007F)));

        byte[] encodedBytes = encoded.readBytes();
        Pixmap decoded = PixmapLoader.load(encodedBytes, 0, encodedBytes.length);

        pixmapTester.checkRenderResult("jng/encode-with-alpha", decoded);
    }

    /** Test the utility function for extracting the alpha channel of a pixmap as a separate image. */
    @Test
    public void testExtractAlpha() {
        Color color = new Color(0x2244667F);
        for (Format format : Format.values()) {
            if (format == Format.Intensity) {
                continue; // Unable to create a pixmap with format 'Intensity' (gets changed to Alpha)
            }

            Pixmap pixmap = pixmapTester.newPixmap(format, color);

            Pixmap alphaPixmap = JngEncoder.extractAlpha(pixmap);
            try {
                int expected;
                if (PixmapUtil.hasAlpha(format)) {
                    expected = 0x7F;
                    if (format == Format.RGBA4444) {
                        // Rounding error due to limited precision
                        expected = 0x77;
                    }
                } else {
                    // For formats without alpha, the resulting alpha pixmap is full white
                    expected = 0xFF;
                }

                int actual = alphaPixmap.getPixel(0, 0) & 0xFF;
                Assert.assertEquals("Format:" + format, expected, actual);
            } finally {
                alphaPixmap.dispose();
            }
        }
    }

    private EncodedImage encode(Pixmap src) throws IOException {
        return encoder.encode(ImageWithDefTester.fromPixmap(src));
    }

}
