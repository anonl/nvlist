package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Dim;

/**
 * Model-view transformation matrices.
 */
public final class GdxViewports {

    private final Viewport screenViewport;
    private final Viewport scene2dViewport;

    public GdxViewports(Dim vsize) {
        screenViewport = new FitViewport(vsize.w, vsize.h);
        scene2dViewport = new FitViewport(vsize.w, vsize.h);
    }

    /**
     * Returns the viewport for rendering to the back buffer.
     * @see #getScene2dViewport()
     */
    public Viewport getScreenViewport() {
        return screenViewport;
    }

    /**
     * Returns the viewport for Scene2d.
     * @see #getScreenViewport()
     */
    public Viewport getScene2dViewport() {
        return scene2dViewport;
    }
}
