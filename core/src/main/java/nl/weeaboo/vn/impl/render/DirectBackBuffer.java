package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.gdx.graphics.GdxViewportUtil;

public final class DirectBackBuffer implements IBackBuffer {

    private final FitViewport screenViewport;
    private final FitViewport scene2dViewport;

    private final SpriteBatch batch;

    public DirectBackBuffer(Dim vsize) {
        screenViewport = new FitViewport(vsize.w, vsize.h);
        scene2dViewport = new FitViewport(vsize.w, vsize.h);

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
        Rect crop = Rect.of(0, 0, windowSize.w, windowSize.h);
        env.updateRenderEnv(crop, windowSize);

        // Update viewports
        GdxViewportUtil.setToOrtho(screenViewport, Dim.of(crop.w, crop.h), true);
        screenViewport.update(windowSize.w, windowSize.h, true);
        scene2dViewport.update(windowSize.w, windowSize.h, true);
    }

    @Override
    public Viewport getScreenViewport() {
        return screenViewport;
    }

    @Override
    public Viewport getScene2dViewport() {
        return scene2dViewport;
    }

}
