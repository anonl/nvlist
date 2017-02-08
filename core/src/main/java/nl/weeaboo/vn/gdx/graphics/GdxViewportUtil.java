package nl.weeaboo.vn.gdx.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Dim;

public final class GdxViewportUtil {

    private GdxViewportUtil() {
    }

    /**
     * Sets the specified viewport to orthographic projection.
     */
    public static void setToOrtho(Viewport viewport, Dim worldSize, boolean ydown) {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(ydown, worldSize.w, worldSize.h);
        viewport.setCamera(camera);
    }

}
