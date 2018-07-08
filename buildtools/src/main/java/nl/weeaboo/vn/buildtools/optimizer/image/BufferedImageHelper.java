package nl.weeaboo.vn.buildtools.optimizer.image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.render.RenderUtil;

public final class BufferedImageHelper {

    private BufferedImageHelper() {
    }

    /**
     * Converts a {@link Pixmap} to an equivalent {@link BufferedImage}. The buffered image type is
     * automatically determined from the pixmap's format.
     *
     * @see #toBufferedImage(Pixmap, int)
     * @see #toBufferedImageType(Format)
     */
    public static BufferedImage toBufferedImage(Pixmap pixmap) {
        return toBufferedImage(pixmap, BufferedImageHelper.toBufferedImageType(pixmap.getFormat()));
    }

    /**
     * Converts a {@link Pixmap} to an equivalent {@link BufferedImage}, using an explicit buffered image
     * type ({@link BufferedImage#getType()}) for the resulting image.
     *
     * @see BufferedImage#getType()
     */
    public static BufferedImage toBufferedImage(Pixmap pixmap, int bufferedImageType) {
        final int iw = pixmap.getWidth();
        final int ih = pixmap.getHeight();

        BufferedImage result = new BufferedImage(iw, ih, bufferedImageType);
        if (pixmap.getFormat() == Format.Alpha) {
            // Pixmap Alpha matches BufferedImage Gray, which requires treating alpha as color
            ByteBuffer alphaBytes = pixmap.getPixels();
            for (int y = 0; y < ih; y++) {
                for (int x = 0; x < iw; x++) {
                    int alpha = alphaBytes.get() & 0xFF;
                    result.setRGB(x, y, (alpha << 24) | (alpha << 16) | (alpha << 8) | alpha);
                }
            }
            alphaBytes.rewind();
        } else {
            for (int y = 0; y < ih; y++) {
                for (int x = 0; x < iw; x++) {
                    int rgba8888 = pixmap.getPixel(x, y);
                    result.setRGB(x, y, RenderUtil.rgba2argb(rgba8888));
                }
            }
        }
        return result;
    }

    /**
     * Returns the equivalent buffered image type ({@link BufferedImage#getType()}) for the given pixmap
     * format.
     */
    public static int toBufferedImageType(Format format) {
        switch (format) {
        case Intensity:
        case Alpha:
            return BufferedImage.TYPE_BYTE_GRAY;
        case RGB565:
        case RGB888:
            return BufferedImage.TYPE_3BYTE_BGR;
        case LuminanceAlpha:
        case RGBA4444:
        case RGBA8888:
            return BufferedImage.TYPE_INT_ARGB;
        default:
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

}
