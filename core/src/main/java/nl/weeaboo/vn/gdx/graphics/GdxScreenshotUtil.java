package nl.weeaboo.vn.gdx.graphics;

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;

import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.impl.image.PixelTextureData;

/**
 * Various low-level functions related to screenshots.
 */
public final class GdxScreenshotUtil {

    private GdxScreenshotUtil() {
    }

    /**
     * Takes a screenshot of the current OpenGL framebuffer.
     *
     * @return A newly allocated pixmap. It's the responsibility of the caller to call
     *         {@link Pixmap#dispose()} on the pixmap when it's no longer needed.
     */
    public static Pixmap screenshot(Rect glRect) {
        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(glRect.x, glRect.y, glRect.w, glRect.h);
        PixmapUtil.flipVertical(pixmap);
        return pixmap;
    }

    /**
     * Returns a reference to the backing pixmap of the given screenshot. This method may fail either because
     * the screenshot has no backing pixmap, or if the specific type of screenshot isn't supported.
     */
    public static @Nullable Pixmap getPixels(IScreenshot screenshot) {
        ITextureData textureData = screenshot.getPixels();
        if (textureData instanceof PixelTextureData) {
            PixelTextureData pixelData = (PixelTextureData)textureData;
            return pixelData.getPixels();
        } else {
            return null;
        }
    }

}
