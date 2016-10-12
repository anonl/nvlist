package nl.weeaboo.vn.image.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.PixmapIO.PNG;

import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.render.RenderUtil;

public final class TestImageUtil {

    static {
        HeadlessGdx.init();
    }

    private TestImageUtil() {
    }

    public static void writePng(Pixmap pixmap, OutputStream out) throws IOException {
        PNG encoder = new PixmapIO.PNG();
        encoder.write(out, pixmap);
    }

    public static PixelTextureData newTestTextureData(int w, int h) {
        return newTestTextureData(0xAA996633, w, h);
    }

    /**
     * @param argb ARGB8888, unassociated alpha
     */
    public static PixelTextureData newTestTextureData(int argb, int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(RenderUtil.argb2rgba(RenderUtil.premultiplyAlpha(argb)));
        pixmap.fill();
        return PixelTextureData.fromPremultipliedPixmap(pixmap);
    }

    public static int toFormat(Format format, int argb) {
        int a = (argb>>24) & 0xFF;
        int r = (argb>>16) & 0xFF;
        int g = (argb>>8 ) & 0xFF;
        int b = (argb    ) & 0xFF;

        switch (format) {
        case Alpha:
            return Color.toIntBits(r, g, b, a);
        case Intensity:
        case LuminanceAlpha:
        case RGB565:
        case RGBA4444:
        case RGB888:
        case RGBA8888:
        default: throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public static void assertEquals(ITextureData a, ITextureData b) {
        assertEquals(((PixelTextureData)a).getPixels(), ((PixelTextureData)b).getPixels());
    }
    public static void assertEquals(Pixmap a, Pixmap b) {
        Assert.assertEquals(a.getWidth(), b.getWidth());
        Assert.assertEquals(a.getHeight(), b.getHeight());
        for (int y = 0; y < a.getHeight(); y++) {
            for (int x = 0; x < a.getWidth(); x++) {
                Assert.assertEquals(a.getPixel(x, y), b.getPixel(x, y));
            }
        }
    }

}
