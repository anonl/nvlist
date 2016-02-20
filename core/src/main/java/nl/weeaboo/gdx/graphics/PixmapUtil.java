package nl.weeaboo.gdx.graphics;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.common.Checks;

public final class PixmapUtil {

    private PixmapUtil() {
    }

    /**
     * @param pixels A Pixmap in {@link Format#RGBA8888}.
     */
    public static void flipVertical(Pixmap pixels) {
        Checks.checkState(pixels.getFormat() == Format.RGBA8888,
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

}
