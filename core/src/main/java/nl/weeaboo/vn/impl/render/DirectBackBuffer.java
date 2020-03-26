package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.gdx.graphics.GdxViewportUtil;

/**
 * Implementation of {@link IBackBuffer} where rendering is done directly to the back buffer.
 */
public final class DirectBackBuffer implements IBackBuffer {

    private final GdxViewports viewports;
    private final SpriteBatch batch;

    public DirectBackBuffer(GdxViewports viewports) {
        this.viewports = Checks.checkNotNull(viewports);

        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public SpriteBatch begin() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Viewport screenViewport = viewports.getScreenViewport();
        batch.setProjectionMatrix(screenViewport.getCamera().combined);

        return batch;
    }

    @Override
    public void end() {
    }

    @Override
    public void flip() {
    }

    @Override
    public void setWindowSize(IEnvironment env, Dim windowSize) {
        // Update viewports
        Viewport screenViewport = viewports.getScreenViewport();
        GdxViewportUtil.setToOrtho(screenViewport, Dim.of(windowSize.w, windowSize.h), true);
        screenViewport.update(windowSize.w, windowSize.h, true);

        Viewport scene2dViewport = viewports.getScene2dViewport();
        scene2dViewport.update(windowSize.w, windowSize.h, true);

        Rect crop = Rect.of(screenViewport.getScreenX(), screenViewport.getScreenY(),
                screenViewport.getScreenWidth(), screenViewport.getScreenHeight());
        env.updateRenderEnv(crop, windowSize);
    }

}
