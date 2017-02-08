package nl.weeaboo.vn.gdx.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;

import nl.weeaboo.common.Rect;

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

}
