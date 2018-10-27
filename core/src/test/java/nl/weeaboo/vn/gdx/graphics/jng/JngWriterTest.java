package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

public final class JngWriterTest {

    private final ExceptionTester exTester = new ExceptionTester();

    private Pixmap pngGray;
    private byte[] pngGrayBytes;

    private Pixmap jpgGray;
    private byte[] jpgGrayBytes;

    private Pixmap jpgColor;
    private byte[] jpgColorBytes;

    private byte[] pngColorBytes;

    private JngWriter jngWriter;

    @Before
    public void before() {
        HeadlessGdx.init();

        pngGray = loadImage("jng/gray.png");
        pngGrayBytes = getImageBytes("jng/gray.png");

        jpgGray = loadImage("jng/gray.jpg");
        jpgGrayBytes = getImageBytes("jng/gray.jpg");

        jpgColor = loadImage("jng/color.jpg");
        jpgColorBytes = getImageBytes("jng/color.jpg");

        pngColorBytes = getImageBytes("jng/color.png");

        jngWriter = new JngWriter();
    }

    /**
     * Write a .jng that has color and PNG alpha data.
     */
    @Test
    public void testColorAndPngAlpha() throws IOException {
        jngWriter.setColorInput(jpgColorBytes);
        jngWriter.setAlphaInput(pngGrayBytes);

        Pixmap merged = PixmapUtil.convert(jpgColor, Format.RGBA8888, false);
        JngReader.insertAlpha(merged, pngGray);

        writeAndCheck(merged);
    }

    /**
     * Write a .jng that has only color, and no alpha channel.
     */
    @Test
    public void testColorAndJpgAlpha() throws IOException {
        jngWriter.setColorInput(jpgColorBytes);
        jngWriter.setAlphaInput(jpgGrayBytes);

        Pixmap merged = PixmapUtil.convert(jpgColor, Format.RGBA8888, false);
        JngReader.insertAlpha(merged, jpgGray);

        writeAndCheck(merged);
    }

    /**
     * Write a .jng that has only color, and no alpha channel.
     */
    @Test
    public void testColorOnly() throws IOException {
        jngWriter.setColorInput(jpgColorBytes);
        writeAndCheck(jpgColor);
    }

    /**
     * Write a .jng that has uses a single-channel (grayscale) colors.
     */
    @Test
    public void testGrayscaleColor() throws IOException {
        // Note: libGDX treats Grayscale .png as Alpha, so we can't easily compare the output .jng with a reference

        // Grascale, no alpha
        jngWriter.setColorInput(jpgGrayBytes);
        write();

        // Grayscale with alpha
        jngWriter.setAlphaInput(pngGrayBytes);
        write();
    }

    private void writeAndCheck(Pixmap expected) throws IOException {
        byte[] jngBytes = write();

        Pixmap actual = JngReader.read(new ByteArrayInputStream(jngBytes), new JngReaderOpts());

        assertImageEquals(expected, actual);
    }

    private byte[] write() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        jngWriter.write(bout);
        return bout.toByteArray();
    }

    /**
     * Test various error conditions that occur when color/alpha are required, but not set yet.
     */
    @Test
    public void testMissingColorOrAlpha() throws IOException {
        // You must set color before alpha
        exTester.expect(IllegalStateException.class, () -> jngWriter.setAlphaInput(jpgGrayBytes));

        // You must set color to be able to write
        exTester.expect(IllegalStateException.class, () -> jngWriter.write(new ByteArrayOutputStream()));

        // Attempting to set malformed .jpg input throws an exception
        ByteBuffer malformedJpeg = ByteBuffer.allocate(JpegHelper.JPEG_MAGIC.length + 1);
        malformedJpeg.put(JpegHelper.JPEG_MAGIC);
        malformedJpeg.put((byte)0);
        exTester.expect(IOException.class, () -> jngWriter.setColorInput(malformedJpeg.array()));

        // Set valid color data so we can now check errors that occur when setting the alpha
        jngWriter.setColorInput(jpgColorBytes);

        // Current limitation: .png alpha must use the 'grayscale' color type
        exTester.expect(IllegalArgumentException.class, () -> jngWriter.setAlphaInput(pngColorBytes));

        // Alpha must be in .png or .jpg format
        exTester.expect(IllegalArgumentException.class, () -> jngWriter.setAlphaInput(new byte[0]));
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
