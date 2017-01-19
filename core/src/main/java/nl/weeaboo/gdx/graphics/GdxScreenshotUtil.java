package nl.weeaboo.gdx.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;

import nl.weeaboo.common.Rect;

public final class GdxScreenshotUtil {

    private GdxScreenshotUtil() {
    }

    public static Pixmap screenshot(Rect glRect) {
        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(glRect.x, glRect.y, glRect.w, glRect.h);
        PixmapUtil.flipVertical(pixmap);
        return pixmap;
    }

}
