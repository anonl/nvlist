package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

public final class JngWriterTest {

    private Pixmap pngAlpha;
    private byte[] pngAlphaBytes;

    private Pixmap jpgAlpha;
    private byte[] jpgAlphaBytes;

    private Pixmap color;
    private byte[] colorBytes;

    private JngWriter jngWriter;

    @Before
    public void before() {
        HeadlessGdx.init();

        pngAlpha = loadImage("jng/alpha.png");
        pngAlphaBytes = getImageBytes("jng/alpha.png");

        jpgAlpha = loadImage("jng/alpha.jpg");
        jpgAlphaBytes = getImageBytes("jng/alpha.jpg");

        color = loadImage("jng/color.jpg");
        colorBytes = getImageBytes("jng/color.jpg");

        jngWriter = new JngWriter();
    }

    /**
     * Write a .jng that has color and PNG alpha data.
     */
    @Test
    public void testColorAndPngAlpha() throws IOException {
        jngWriter.setColorInput(colorBytes);
        jngWriter.setAlphaInput(pngAlphaBytes);

        Pixmap merged = PixmapUtil.convert(color, Format.RGBA8888, false);
        JngReader.insertAlpha(merged, pngAlpha);

        writeAndCheck(merged);
    }

    /**
     * Write a .jng that has only color, and no alpha channel.
     */
    @Test
    public void testColorAndJpgAlpha() throws IOException {
        jngWriter.setColorInput(colorBytes);
        jngWriter.setAlphaInput(jpgAlphaBytes);

        Pixmap merged = PixmapUtil.convert(color, Format.RGBA8888, false);
        JngReader.insertAlpha(merged, jpgAlpha);

        writeAndCheck(merged);
    }

    /**
     * Write a .jng that has only color, and no alpha channel.
     */
    @Test
    public void testColorOnly() throws IOException {
        jngWriter.setColorInput(colorBytes);
        writeAndCheck(color);
    }

    private void writeAndCheck(Pixmap expected) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        jngWriter.write(bout);
        byte[] jngBytes = bout.toByteArray();

        Pixmap actual = JngReader.read(new ByteArrayInputStream(jngBytes), new JngReaderOpts());

        assertImageEquals(expected, actual);
    }

    private static byte[] getImageBytes(String path) {
        return Gdx.files.classpath(path).readBytes();
    }

    private static Pixmap loadImage(String path) {
        return new Pixmap(Gdx.files.classpath(path));
    }

    private static void assertImageEquals(Pixmap expected, Pixmap actual) {
        // PixmapIO.writePNG(Gdx.files.local("out.png"), actual);

        PixmapEquality pixmapEquality = new PixmapEquality();
        pixmapEquality.setMaxColorDiff(16);
        pixmapEquality.assertEquals(expected, actual);
    }

}
