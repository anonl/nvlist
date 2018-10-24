package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapTester;

public class JngReaderTest {

    private static JngTestSuite testSuite;

    private final PixmapTester pixmapTester = new PixmapTester();
    private final ExceptionTester exTester = new ExceptionTester();

    @BeforeClass
    public static void beforeClass() throws IOException {
        HeadlessGdx.init();
        testSuite = JngTestSuite.open();
    }

    @AfterClass
    public static void afterClass() {
        testSuite.dispose();
    }

    @After
    public void after() {
        pixmapTester.dispose();
    }

    @Test
    public void testSuite() throws IOException {
        assertImage("C1CBN0.jng", 180, 110);
        assertImage("C2CBN0.jng", 180, 110);
        assertImage("C3CBN0.jng", 180, 110);
        assertImage("C4CBN0.jng", 180, 110);
        assertImage("C6CBN0.jng", 180, 110);

        assertImage("G1CBN0.jng", 180, 121);
        assertImage("G2CBN0.jng", 180, 121);
        assertImage("G3CBN0.jng", 180, 121);
        assertImage("G4CBN0.jng", 180, 121);
        assertImage("G6CBN0.jng", 180, 121);

        assertImage("I1CBN0.jng", 160, 234);
        assertImage("I2CBN0.jng", 160, 234);
        assertImage("I3CBN0.jng", 160, 234);
        assertImage("I4CBN0.jng", 160, 234);

        assertImage("TCBA8S.jng", 160, 120);
        assertImage("TCBN0S.jng", 160, 120);
        assertImage("TCOA8S.jng", 160, 120);
        assertImage("TCON0S.jng", 160, 120);
        assertImage("TCPA8L.jng", 640, 480);
        assertImage("TCPA8S.jng", 160, 120);
        assertImage("TCPN0L.jng", 640, 480);
        assertImage("TCPN0S.jng", 160, 120);

        assertImage("TGBA1S.jng", 160, 120);
        assertImage("TGBN0S.jng", 160, 120);
        assertImage("TGOA1S.jng", 160, 120);
        assertImage("TGON0S.jng", 160, 120);
        assertImage("TGPA1L.jng", 640, 480);
        assertImage("TGPA1S.jng", 160, 120);
        assertImage("TGPN0L.jng", 640, 480);
        assertImage("TGPN0S.jng", 160, 120);
    }

    @Test
    public void testInsertAlpha() {
        Pixmap alpha = pixmapTester.newPixmap(Format.Alpha, new Color(1, 1, 1, 0));

        // Insert alpha into RGBA8888 image
        Pixmap rgba8888 = pixmapTester.newPixmap(Format.RGBA8888, new Color(0x20406080));
        Assert.assertEquals("20406080", Integer.toHexString(rgba8888.getPixel(0, 0)));
        JngReader.insertAlpha(rgba8888, alpha);
        Assert.assertEquals("20406000", Integer.toHexString(rgba8888.getPixel(0, 0)));

        // Insert alpha into RGBA4444 image
        Pixmap rgba4444 = pixmapTester.newPixmap(Format.RGBA4444, new Color(0x20406080));
        // Due to lack of precision in RGBA4444, the RGBA we read back is not what we put in
        Assert.assertEquals("22446688", Integer.toHexString(rgba4444.getPixel(0, 0)));
        JngReader.insertAlpha(rgba4444, alpha);
        Assert.assertEquals("22446600", Integer.toHexString(rgba4444.getPixel(0, 0)));
    }

    /**
     * Check the supported color/alpha image formats for {@link JngReader#insertAlpha(Pixmap, Pixmap)}.
     */
    @Test
    public void testInsertAlphaFormats() {
        Pixmap rgba8888 = pixmapTester.newPixmap(Format.RGBA8888, Color.RED);
        Pixmap intensity = pixmapTester.newPixmap(Format.Intensity, Color.RED);

        ImmutableSet<Format> supportedAlphaFormats = ImmutableSet.of(Format.Alpha, Format.Intensity);
        ImmutableSet<Format> supportedColorFormats = ImmutableSet.of(Format.RGBA8888, Format.RGBA4444);

        for (Format alphaFormat : Format.values()) {
            Pixmap alpha = pixmapTester.newPixmap(alphaFormat, Color.WHITE);
            if (supportedAlphaFormats.contains(alphaFormat)) {
                JngReader.insertAlpha(rgba8888, alpha);
            } else {
                exTester.expect(IllegalArgumentException.class, () -> JngReader.insertAlpha(rgba8888, alpha));
            }
        }

        for (Format colorFormat : Format.values()) {
            Pixmap color = pixmapTester.newPixmap(colorFormat, Color.WHITE);
            if (supportedColorFormats.contains(colorFormat)) {
                JngReader.insertAlpha(color, intensity);
            } else {
                exTester.expect(IllegalArgumentException.class, () -> JngReader.insertAlpha(color, intensity));
            }
        }
    }

    /**
     * Attempts to read non-JNG data should throw a parse exception.
     */
    @Test
    public void testReadInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[32]);
        exTester.expect(JngParseException.class, () -> JngReader.read(in, new JngReaderOpts()));
    }

    private void assertImage(String path, int w, int h) throws IOException {
        Pixmap pixmap = testSuite.loadImage(path);
        Assert.assertEquals(w, pixmap.getWidth());
        Assert.assertEquals(h, pixmap.getHeight());
    }

}
