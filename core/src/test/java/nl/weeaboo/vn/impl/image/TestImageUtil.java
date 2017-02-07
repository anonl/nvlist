package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.io.OutputStream;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.PixmapIO.PNG;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.render.RenderUtil;

public final class TestImageUtil {

    static {
        HeadlessGdx.init();
    }

    private TestImageUtil() {
    }

    /**
     * Writes a pixmap to PNG.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public static void writePng(Pixmap pixmap, OutputStream out) throws IOException {
        PNG encoder = new PixmapIO.PNG();
        encoder.write(out, pixmap);
    }

    /** Creates a new {@code w x h} texture data object filled with a dummy color. */
    public static PixelTextureData newTestTextureData(int w, int h) {
        return newTestTextureData(0xAA996633, w, h);
    }

    /**
     * Creates a new {@code w x h} texture data object filled with the specified color.
     * @param argb ARGB8888, unassociated alpha
     */
    public static PixelTextureData newTestTextureData(int argb, int w, int h) {
        Pixmap pixmap = PixmapUtil.newUninitializedPixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(RenderUtil.argb2rgba(RenderUtil.premultiplyAlpha(argb)));
        pixmap.fill();
        return PixelTextureData.fromPremultipliedPixmap(pixmap);
    }

    /**
     * Asserts that two texture data objects contain equal pixel data.
     */
    public static void assertEquals(ITextureData a, ITextureData b) {
        Pixmap pixelsA = ((PixelTextureData)a).getPixels();
        Pixmap pixelsB = ((PixelTextureData)b).getPixels();
        new PixmapEquality().assertEquals(pixelsA, pixelsB);
    }

}
