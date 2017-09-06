package nl.weeaboo.vn.buildtools.optimizer.image;

import java.awt.image.BufferedImage;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.render.RenderUtil;

public final class BufferedImageHelper {

    private BufferedImageHelper() {
    }

    public static BufferedImage toBufferedImage(Pixmap pixmap, int bufferedImageType) {
        final int iw = pixmap.getWidth();
        final int ih = pixmap.getHeight();

        BufferedImage result = new BufferedImage(iw, ih, bufferedImageType);
        for (int y = 0; y < ih; y++) {
            for (int x = 0; x < iw; x++) {
                int rgba8888 = pixmap.getPixel(x, y);
                result.setRGB(x, y, RenderUtil.rgba2argb(rgba8888));
            }
        }
        return result;
    }

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
