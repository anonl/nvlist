package nl.weeaboo.vn.gdx.graphics;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.BufferUtils;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;

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

        int iw = source.getWidth();
        int ih = source.getHeight();
        Pixmap result = newUninitializedPixmap(iw, ih, targetFormat);
        copySubRect(source, Rect.of(0, 0, iw, ih), result, Rect.of(0, 0, iw, ih), Filter.NearestNeighbour);

        if (disposeSource) {
            source.dispose();
        }
        return result;
    }

    /**
     * Takes a sub-rect {@code srcRect} from {@code src} and copies it to a sub-rect {@code dstRect} in {@code dst}.
     *
     * @param filter Determines which type of interpolation to use if the src/dst rects are a different size.
     */
    public static void copySubRect(Pixmap src, Rect srcRect, Pixmap dst, Rect dstRect, Filter filter) {
        Blending oldBlend = dst.getBlending();
        Filter oldFilter = Filter.BiLinear;
        try {
            dst.setBlending(Blending.None);
            dst.setFilter(filter);

            dst.drawPixmap(src, srcRect.x, srcRect.y, srcRect.w, srcRect.h,
                    dstRect.x, dstRect.y, dstRect.w, dstRect.h);
        } finally {
            dst.setBlending(oldBlend);
            dst.setFilter(oldFilter);
        }
    }

    /** Creates an identical copy (including color format) of the given pixmap. */
    public static Pixmap copy(Pixmap original) {
        Pixmap copy = newUninitializedPixmap(original.getWidth(), original.getHeight(), original.getFormat());
        copy(original, copy);
        return copy;
    }

    /**
     * Copies the contents of {@code src} to {@code dst}.
     *
     * @throws IllegalArgumentException If the pixmaps are different sizes or different formats.
     */
    public static void copy(Pixmap src, Pixmap dst) {
        Format srcFmt = src.getFormat();
        Format dstFmt = dst.getFormat();
        Checks.checkArgument(srcFmt == dstFmt, "Formats not equal: src=" + srcFmt + ", dst=" + dstFmt);

        int srcW = src.getWidth();
        int dstW = dst.getWidth();
        Checks.checkArgument(srcW == dstW, "Widths not equal: src.w=" + srcW + ", dst.w=" + dstW);

        int srcH = src.getHeight();
        int dstH = src.getHeight();
        Checks.checkArgument(srcH == dstH, "Heights not equal: src.h=" + srcH + ", dst.h=" + dstH);

        ByteBuffer srcPixels = src.getPixels();
        ByteBuffer dstPixels = dst.getPixels();
        BufferUtils.copy(srcPixels, dstPixels, srcPixels.limit());
        srcPixels.clear();
        dstPixels.clear();
    }

    /**
     * Creates a scaled copy of a pixmap.
     *
     * @see #copy(Pixmap)
     * @see #copySubRect(Pixmap, Rect, Pixmap, Rect, Filter)
     */
    public static Pixmap resizedCopy(Pixmap src, Dim dstSize, Filter filter) {
        Pixmap copy = newUninitializedPixmap(dstSize.w, dstSize.h, src.getFormat());
        copySubRect(src, Rect.of(0, 0, src.getWidth(), src.getHeight()), // src rect
                copy, Rect.of(0, 0, dstSize.w, dstSize.h), // dst rect
                filter);
        return copy;
    }

    /**
     * Allocates a new {@link Pixmap} without zeroing its memory like the regular constructor
     * ({@link Pixmap#Pixmap(int, int, Format)})
     */
    public static Pixmap newUninitializedPixmap(int width, int height, Format format) {
        Gdx2DPixmap gdx2dPixmap = new Gdx2DPixmap(width, height,
                Format.toGdx2DPixmapFormat(format));
        return new Pixmap(gdx2dPixmap);
    }

}
