package nl.weeaboo.vn.gdx.graphics;

import javax.annotation.CheckForNull;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;

import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.impl.image.PixelTextureData;

public final class GdxScreenshotUtil {

    private GdxScreenshotUtil() {
    }

    /**
     * Takes a screenshot of the current OpenGL framebuffer.
     */
    public static Pixmap screenshot(Rect glRect) {
        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(glRect.x, glRect.y, glRect.w, glRect.h);
        PixmapUtil.flipVertical(pixmap);
        return pixmap;
    }

    @CheckForNull
    public static Pixmap getPixels(IScreenshot screenshot) {
        ITextureData textureData = screenshot.getPixels();
        if (textureData instanceof PixelTextureData) {
            PixelTextureData pixelData = (PixelTextureData)textureData;
            return pixelData.getPixels();
        } else {
            return null;
        }
    }

}
