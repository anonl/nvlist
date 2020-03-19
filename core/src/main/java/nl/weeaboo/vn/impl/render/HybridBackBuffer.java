package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.core.IEnvironment;

/**
 * Hybrid {@link IBackBuffer} implementation which switches between {@link FboBackBuffer} and
 * {@link DirectBackBuffer} based on the current environment (screen size, performance).
 */
public final class HybridBackBuffer implements IBackBuffer {

    private final Dim vsize;
    private final DirectBackBuffer direct;
    private final FboBackBuffer fbo;

    private IBackBuffer active;

    public HybridBackBuffer(Dim vsize, GdxViewports viewports) {
        this.vsize = Checks.checkNotNull(vsize);

        direct = new DirectBackBuffer(viewports);
        fbo = new FboBackBuffer(vsize, viewports);

        active = direct;
    }

    @Override
    public void dispose() {
        direct.dispose();
        fbo.dispose();
    }

    @Override
    public SpriteBatch begin() {
        return active.begin();
    }

    @Override
    public void end() {
        active.end();
    }

    @Override
    public void flip() {
        active.flip();
    }

    @Override
    public void setWindowSize(IEnvironment env, Dim windowSize) {
        if (windowSize.equals(vsize)) {
            // Window is exact size -- using an FBO is redundant
            active = direct;
        } else if (windowMuchLarger(windowSize)) {
            // Window is larger than vsize, rendering to an FBO might be faster (render fewer pixels)
            active = fbo;
        } else if (isLowEndSystem()) {
            /*
             * Render directly to the screen on low-end systems (faster, but may introduce small scaling
             * artifacts)
             */
            active = direct;
        } else {
            active = fbo;
        }

        active.setWindowSize(env, windowSize);
    }

    private boolean isLowEndSystem() {
        ApplicationType appType = Gdx.app.getType();
        return appType == ApplicationType.Android || appType == ApplicationType.iOS;
    }

    /**
     * Returns {@code true} if the given window size is much larger than the virtual size.
     */
    private boolean windowMuchLarger(Dim windowSize) {
        if (windowSize.w <= vsize.w || windowSize.h <= vsize.h) {
            return false;
        }

        int windowPixels = windowSize.w * windowSize.h;
        int vsizePixels = vsize.w * vsize.h;
        return windowPixels >= vsizePixels * 1.5;
    }

    @Override
    public String toString() {
        return "HybridBackBuffer[" + active + "]";
    }

}
