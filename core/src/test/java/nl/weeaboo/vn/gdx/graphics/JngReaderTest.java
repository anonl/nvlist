package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.vn.gdx.HeadlessGdx;

public class JngReaderTest {

    private static JngTestSuite testSuite;

    @BeforeClass
    public static void beforeClass() throws IOException {
        HeadlessGdx.init();
        testSuite = JngTestSuite.open();
    }

    @AfterClass
    public static void afterClass() {
        testSuite.dispose();
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
        Pixmap pixmap = testSuite.loadImage(path);
        Assert.assertEquals(w, pixmap.getWidth());
        Assert.assertEquals(h, pixmap.getHeight());
    }

}
