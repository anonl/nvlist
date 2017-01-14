package nl.weeaboo.gdx.graphics.blur;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.base.Preconditions;

public final class ImageBlur {

    /** Division lookup table */
    private byte[] divLut = {};

    /** Blur radius */
    private int radius;

    public ImageBlur() {
    }

    public void setRadius(int r) {
        Preconditions.checkArgument(r >= 0);

        if (radius != r) {
            radius = r;

            divLut = generateDivLut(r);
        }
    }

    public void applyBlur(Pixmap image) {
        Preconditions.checkArgument(image.getFormat() == Format.RGBA8888,
                "Unsupported format: " + image.getFormat());

        final int w = image.getWidth();
        final int h = image.getHeight();

        // Get access to image pixels packed as RGB+alpha in a single int
        ByteBuffer buffer = image.getPixels();
        buffer.limit(4 * w * h);
        IntBuffer pixels = buffer.asIntBuffer();

        int[] tempBuffer = new int[Math.max(w, h)];

        // Blur horizontal
        for (int y = 0, pos = 0; y < h; y++, pos += w) {
            blurLine(tempBuffer, w, pixels, pos, 1);
        }

        // Blur vertical (these two 1D blurs together give the same result as a 2D box blur)
        for (int x = 0; x < w; x++) {
            blurLine(tempBuffer, h, pixels, x, w);
        }
    }

    private void blurLine(int[] lineBuffer, int lineLength, IntBuffer image, int srcOff, int srcStep) {
        // a1-a4 are accumulator variables. These hold the sum of per-channel pixel values in the kernel.
        // Initialize accumlator for the first pixel
        int firstPixel = image.get(srcOff);
        int a1 = (radius + 1) * getC1(firstPixel);
        int a2 = (radius + 1) * getC2(firstPixel);
        int a3 = (radius + 1) * getC3(firstPixel);
        int a4 = (radius + 1) * getC4(firstPixel);
        for (int d = 1; d <= radius; d++) {
            int rgba = image.get(srcOff + srcStep * Math.min(d, lineLength - 1));

            a1 += getC1(rgba);
            a2 += getC2(rgba);
            a3 += getC3(rgba);
            a4 += getC4(rgba);
        }

        /*
         * For each subsequent pixel, we can update the sum by subtracting the pixel that leaves our kernel
         * and then adding the pixel that enters our kernel
         */
        for (int i = 0; i < lineLength; i++) {
            lineBuffer[i] = ((divLut[a1] & 0xFF) << 24)
                          | ((divLut[a2] & 0xFF) << 16)
                          | ((divLut[a3] & 0xFF) << 8)
                          |  (divLut[a4] & 0xFF);

            int prev = image.get(srcOff + srcStep * Math.max(0, i - radius));
            int next = image.get(srcOff + srcStep * Math.min(i + radius + 1, lineLength - 1));

            a1 += getC1(next) - getC1(prev);
            a2 += getC2(next) - getC2(prev);
            a3 += getC3(next) - getC3(prev);
            a4 += getC4(next) - getC4(prev);
        }

        // Copy line buffer into pixmap
        for (int n = 0, p = srcOff; n < lineLength; n++, p += srcStep) {
            image.put(p, lineBuffer[n]);
        }
    }

    private static byte[] generateDivLut(int radius) {
        int kernelSize = getKernelSize(radius);
        byte[] lut = new byte[256 * kernelSize];
        for (int n = 0; n < lut.length; n += kernelSize) {
            int v = n / kernelSize;
            for (int x = 0; x < kernelSize; x++) {
                lut[n + x] = (byte)v;
            }
        }
        return lut;
    }

    private static int getKernelSize(int radius) {
        return radius + radius + 1;
    }

    private static int getC4(int rgba) {
        return rgba & 0xFF;
    }
    private static int getC3(int rgba) {
        return (rgba >> 8) & 0xFF;
    }
    private static int getC2(int rgba) {
        return (rgba >> 16) & 0xFF;
    }
    private static int getC1(int rgba) {
        return (rgba >> 24) & 0xFF;
    }

}
