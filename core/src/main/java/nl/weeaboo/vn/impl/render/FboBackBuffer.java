package nl.weeaboo.vn.impl.render;

import javax.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.vn.gdx.res.DisposeUtil;

public final class FboBackBuffer implements IBackBuffer {

    private final Dim vsize;

    private final FitViewport frameBufferViewport;
    private final FitViewport screenViewport;
    private final FitViewport scene2dViewport;

    private final SpriteBatch batch;
    private @Nullable FrameBuffer frameBuffer;

    public FboBackBuffer(Dim vsize) {
        this.vsize = Checks.checkNotNull(vsize);

        frameBufferViewport = new FitViewport(vsize.w, vsize.h);
        screenViewport = new FitViewport(vsize.w, vsize.h);
        scene2dViewport = new FitViewport(vsize.w, vsize.h);

        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        disposeFrameBuffer();

        batch.dispose();
    }

    @Override
    public SpriteBatch begin() {
        checkFboExists();

        frameBuffer.begin();
        frameBufferViewport.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Camera camera = frameBufferViewport.getCamera();
        batch.setProjectionMatrix(camera.combined);

        return batch;
    }

    @Override
    public void end() {
        checkFboExists();

        frameBuffer.end();
    }

    @Override
    public void flip() {
        checkFboExists();

        screenViewport.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenViewport.getCamera().combined);

        batch.begin();
        batch.disableBlending();
        try {
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, vsize.w, vsize.h);
        } finally {
            batch.enableBlending();
            batch.end();
        }
    }

    private void checkFboExists() {
        Checks.checkState(frameBuffer != null, "FrameBuffer doesn't exist; call setWindowSize() first");
    }

    private void disposeFrameBuffer() {
        frameBuffer = DisposeUtil.dispose(frameBuffer);
    }

    @Override
    public void setWindowSize(IEnvironment env, Dim windowSize) {
        disposeFrameBuffer();

        Dim fboSize = Dim.of(vsize.w / 2, vsize.h / 2);

        env.updateRenderEnv(Rect.of(0, 0, fboSize.w, fboSize.h), fboSize);

        // Update viewports
        GdxViewportUtil.setToOrtho(screenViewport, fboSize, true);
        screenViewport.update(windowSize.w, windowSize.h, true);
        scene2dViewport.update(windowSize.w, windowSize.h, true);

        // (Re)init screensize-related resources
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, fboSize.w, fboSize.h, false);
        GdxViewportUtil.setToOrtho(frameBufferViewport, fboSize, true);
        frameBufferViewport.update(fboSize.w, fboSize.h, true);
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
