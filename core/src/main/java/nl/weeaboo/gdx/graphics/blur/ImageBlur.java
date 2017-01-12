package nl.weeaboo.gdx.graphics.blur;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.base.Preconditions;

public final class ImageBlur {

    private ImageBlur() {
    }

    public static void blur(Pixmap image, int radius) {
        Preconditions.checkArgument(image.getFormat() == Format.RGBA8888,
                "Unsupported format: " + image.getFormat());

        /** Division lookup table */
        int[] divLut = generateDivLut(radius);

        // Box blur horizontal, then vertical (gives the same result as a 2D box blur)
        blurH(image, radius, divLut);

        // TODO: Perform vertical blur
        // blurV(image, radius);
    }

    private static void blurH(Pixmap pixmap, int radius, int[] divLut) {
        final int w = pixmap.getWidth();
        final int h = pixmap.getHeight();

        ByteBuffer buffer = pixmap.getPixels();
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.limit(4 * w * h);
        IntBuffer pixels = buffer.asIntBuffer();

        /** Line output buffer */
        int[] out = new int[w];

        int pos = 0;
        for (int y = 0; y < h; y++) {
            // Initialze accumulator
            int accumA = 0, accumR = 0, accumG = 0, accumB = 0;
            for (int d = -radius; d <= radius; d++) {
                int rgba = pixels.get(pos + Math.max(0, Math.min(d, w-1)));

                accumR += getRed(rgba);
                accumG += getGreen(rgba);
                accumB += getBlue(rgba);
                accumA += getAlpha(rgba);
            }

            for (int x = 0; x < w; x++) {
                out[x] = (divLut[accumR]<<24) | (divLut[accumG]<<16) | (divLut[accumB]<<8) | divLut[accumA];

                int prev = pixels.get(pos + Math.max(0, x - radius));
                int next = pixels.get(pos + Math.min(x + radius + 1, w-1));

                accumR += getRed(next) - getRed(prev);
                accumG += getGreen(next) - getGreen(prev);
                accumB += getBlue(next) - getBlue(prev);
                accumA += getAlpha(next) - getAlpha(prev);
            }

            // Copy line buffer into pixmap
            for (int n = 0; n < out.length; n++) {
                pixels.put(pos + n, out[n]);
            }

            pos += w;
        }
    }

    private static int[] generateDivLut(int radius) {
        int kernelSize = radius + radius + 1;
        int[] lut = new int[256 * kernelSize];
        for (int n = 0; n < lut.length; n += kernelSize) {
            int v = n / kernelSize;
            for (int x = 0; x < kernelSize; x++) {
                lut[n + x] = v;
            }
        }
        return lut;
    }

    private static int getAlpha(int rgba) {
        return rgba & 0xFF;
    }
    private static int getBlue(int rgba) {
        return (rgba >> 8) & 0xFF;
    }
    private static int getGreen(int rgba) {
        return (rgba >> 16) & 0xFF;
    }
    private static int getRed(int rgba) {
        return (rgba >> 24) & 0xFF;
    }

}
