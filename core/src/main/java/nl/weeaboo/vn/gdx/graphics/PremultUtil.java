package nl.weeaboo.vn.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;

/**
 * Utility functions for working with premultiplied alpha.
 */
public final class PremultUtil {

    /**
     * Converts an RGBA8888 pixmap with unassociated alpha to premultiplied alpha.
     */
    public static void premultiplyAlpha(Pixmap pixmap) {
        Format format = pixmap.getFormat();
        switch (format) {
        case RGB565:
        case RGB888:
            break; // Format doesn't have alpha, so nothing to do
        case Intensity:
            break; // GDX treats incorrectly treats INTENSITY as ALPHA
        case Alpha:
            break; // Format only has alpha, so nothing to do
        case LuminanceAlpha: {
            final ByteBuffer pixels = pixmap.getPixels();
            final int limit = pixmap.getWidth() * pixmap.getHeight() * 2;
            for (int n = 0; n < limit; n += 2) {
                int i = pixels.get(n    ) & 0xFF;
                int a = pixels.get(n + 1) & 0xFF;

                i = (a * i + 127) / 255;

                pixels.put(n, (byte)i);
            }
        } break;
        case RGBA4444: {
            final ByteBuffer byteBuffer = pixmap.getPixels();
            // RGBA4444 is stored as shorts in native order (see Pixmap#getPixels)
            byteBuffer.order(ByteOrder.nativeOrder());
            ShortBuffer pixels = byteBuffer.asShortBuffer();

            final int limit = pixmap.getWidth() * pixmap.getHeight();
            for (int n = 0; n < limit; n++) {
                int rgba16 = pixels.get(n);

                int r = (rgba16 >> 12) & 0xF;
                int g = (rgba16 >> 8 ) & 0xF;
                int b = (rgba16 >> 4 ) & 0xF;
                int a = (rgba16      ) & 0xF;

                r = (a * r + 7) / 15;
                g = (a * g + 7) / 15;
                b = (a * b + 7) / 15;

                pixels.put(n, (short)((r << 12) | (g << 8) | (b << 4) | a));
            }
        } break;
        case RGBA8888: {
            final ByteBuffer pixels = pixmap.getPixels();
            final int limit = pixmap.getWidth() * pixmap.getHeight() * 4;
            for (int n = 0; n < limit; n += 4) {
                int r = pixels.get(n    ) & 0xFF;
                int g = pixels.get(n + 1) & 0xFF;
                int b = pixels.get(n + 2) & 0xFF;
                int a = pixels.get(n + 3) & 0xFF;

                r = (a * r + 127) / 255;
                g = (a * g + 127) / 255;
                b = (a * b + 127) / 255;

                pixels.put(n    , (byte)r);
                pixels.put(n + 1, (byte)g);
                pixels.put(n + 2, (byte)b);
            }
        } break;
        default:
            throw new IllegalArgumentException("Pixmap with unsupported format: " + format);
        }
    }

    /**
     * Converts an RGBA8888 pixmap with premultiplied alpha to unassociated alpha.
     */
    private static void unpremultiplyAlpha(Pixmap pixmap) {
        Format format = pixmap.getFormat();
        switch (format) {
        case RGB565:
        case RGB888:
            break; // Format doesn't have alpha, so nothing to do
        case Intensity:
            break; // GDX treats incorrectly treats INTENSITY as ALPHA
        case Alpha:
            break; // Format only has alpha, so nothing to do
        case LuminanceAlpha: {
            final ByteBuffer pixels = pixmap.getPixels();
            final int limit = pixmap.getWidth() * pixmap.getHeight() * 2;
            for (int n = 0; n < limit; n += 2) {
                int i = pixels.get(n    ) & 0xFF;
                int a = pixels.get(n + 1) & 0xFF;

                if (a == 0) {
                    i = 0;
                } else {
                    int halfA = a >> 1;
                    i = (255 * i + halfA) / a;
                }

                pixels.put(n, (byte)i);
            }
        } break;
        case RGBA4444: {
            final ByteBuffer byteBuffer = pixmap.getPixels();
            // RGBA4444 is stored as shorts in native order (see Pixmap#getPixels)
            byteBuffer.order(ByteOrder.nativeOrder());
            ShortBuffer pixels = byteBuffer.asShortBuffer();

            final int limit = pixmap.getWidth() * pixmap.getHeight();
            for (int n = 0; n < limit; n++) {
                int rgba16 = pixels.get(n);

                int r = (rgba16 >> 12) & 0xF;
                int g = (rgba16 >> 8 ) & 0xF;
                int b = (rgba16 >> 4 ) & 0xF;
                int a = (rgba16      ) & 0xF;

                if (a == 0) {
                    r = 0;
                    g = 0;
                    b = 0;
                } else {
                    int halfA = a >> 1;
                    r = (15 * r + halfA) / a;
                    g = (15 * g + halfA) / a;
                    b = (15 * b + halfA) / a;
                }

                pixels.put(n, (short)((r << 12) | (g << 8) | (b << 4) | a));
            }
        } break;
        case RGBA8888: {
            final ByteBuffer pixels = pixmap.getPixels();
            final int limit = pixmap.getWidth() * pixmap.getHeight() * 4;
            for (int n = 0; n < limit; n += 4) {
                int r = pixels.get(n    ) & 0xFF;
                int g = pixels.get(n + 1) & 0xFF;
                int b = pixels.get(n + 2) & 0xFF;
                int a = pixels.get(n + 3) & 0xFF;

                if (a == 0) {
                    r = 0;
                    g = 0;
                    b = 0;
                } else {
                    int halfA = a >> 1;
                    r = (255 * r + halfA) / a;
                    g = (255 * g + halfA) / a;
                    b = (255 * b + halfA) / a;
                }

                pixels.put(n    , (byte)r);
                pixels.put(n + 1, (byte)g);
                pixels.put(n + 2, (byte)b);
            }
        } break;
        default:
            throw new IllegalArgumentException("Pixmap with unsupported format: " + format);
        }
    }

    /**
     * Writes a pixmap with premultiplied alpha to a PNG file. This is non-trivial because PNG only supports
     * unassociated alpha, so a color conversion is needed.
     *
     * @param premultPixmap A pixmap with premultiplied alpha.
     */
    public static void writePremultPng(FileHandle fileHandle, Pixmap premultPixmap) {
        Pixmap pngPixmap = PixmapUtil.copy(premultPixmap);
        unpremultiplyAlpha(pngPixmap);
        PixmapIO.writePNG(fileHandle, pngPixmap);
    }

}
