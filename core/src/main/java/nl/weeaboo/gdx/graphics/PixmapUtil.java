package nl.weeaboo.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.common.Checks;

public final class PixmapUtil {

    private PixmapUtil() {
    }

    /**
     * @param pixels A Pixmap in {@link Format#RGBA8888}.
     */
    public static void flipVertical(Pixmap pixels) {
        Checks.checkArgument(pixels.getFormat() == Format.RGBA8888,
                "Pixmap with unsupported format: " + pixels.getFormat());

        int bytesPerRow = pixels.getWidth() * 4; // RGBA8888
        int h = pixels.getHeight();
        byte[] lineBuffer0 = new byte[bytesPerRow];
        byte[] lineBuffer1 = new byte[bytesPerRow];
        ByteBuffer pixelsBuffer = pixels.getPixels();
        for (int y = 0; y < h / 2; y++) {
            int y0 = y * bytesPerRow;
            int y1 = (h - 1 - y) * bytesPerRow;

            // Swap pixels in rows
            pixelsBuffer.position(y0);
            pixelsBuffer.get(lineBuffer0);
            pixelsBuffer.position(y1);
            pixelsBuffer.get(lineBuffer1);
            pixelsBuffer.position(y1);
            pixelsBuffer.put(lineBuffer0);
            pixelsBuffer.position(y0);
            pixelsBuffer.put(lineBuffer1);
        }
        pixelsBuffer.rewind();
    }

    /**
     * Converts the given pixmap to the specified target color format. If the source pixmap is already in the
     * correct format, the original pixmap is returned unmodified.
     */
    public static Pixmap convert(Pixmap source, Format targetFormat, boolean disposeSource) {
        if (source.getFormat() == targetFormat) {
            return source; // Already the correct format
        }

        Blending oldBlend = Pixmap.getBlending();
        Pixmap result;
        try {
            result = new Pixmap(source.getWidth(), source.getHeight(), targetFormat);
            Pixmap.setBlending(Blending.None);
            result.drawPixmap(source, 0, 0, 0, 0, source.getWidth(), source.getHeight());
        } finally {
            Pixmap.setBlending(oldBlend);
            if (disposeSource) {
                source.dispose();
            }
        }
        return result;
    }

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

                pixels.put(n, (short)((r<<12) | (g<<8) | (b<<4) | a));
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

}
