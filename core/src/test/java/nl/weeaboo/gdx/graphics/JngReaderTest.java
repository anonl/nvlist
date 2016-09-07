package nl.weeaboo.gdx.graphics;

import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.io.Resources;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.ZipFileArchive;
import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.io.RandomAccessUtil;

public class JngReaderTest {

    static {
        HeadlessGdx.init();
    }

    private static final Logger LOG = LoggerFactory.getLogger(JngReaderTest.class);

    private static ZipFileArchive archive;

    @BeforeClass
    public static void beforeClass() throws IOException {
        byte[] zipBytes = Resources.toByteArray(JngReaderTest.class.getResource("/jng/JNGsuite-20021214.zip"));

        archive = new ZipFileArchive();
        archive.open(RandomAccessUtil.wrap(zipBytes, 0, zipBytes.length));
    }

    @AfterClass
    public static void afterClass() {
        archive.close();
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

    private void assertImage(String path, int w, int h) throws IOException {
        Pixmap pixmap = loadImage(path);
        Assert.assertEquals(w, pixmap.getWidth());
        Assert.assertEquals(h, pixmap.getHeight());
    }

    private Pixmap loadImage(String path) throws IOException {
        InputStream in = archive.openInputStream(FilePath.of(path));
        try {
            LOG.info("Reading JNG file: {}", path);
            return JngReader.read(in);
        } finally {
            in.close();
        }
    }

}
