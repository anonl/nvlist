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
    private final GdxViewports viewports;

    private final FitViewport frameBufferViewport;

    private final SpriteBatch batch;
    private @Nullable FrameBuffer frameBuffer;

    public FboBackBuffer(Dim vsize, GdxViewports viewports) {
        this.vsize = Checks.checkNotNull(vsize);
        this.viewports = Checks.checkNotNull(viewports);

        frameBufferViewport = new FitViewport(vsize.w, vsize.h);
        batch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        disposeFrameBuffer();

        batch.dispose();
    }

    @Override
    public SpriteBatch begin() {
        FrameBuffer frameBuffer = checkFboExists();

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
        FrameBuffer frameBuffer = checkFboExists();

        frameBuffer.end();
    }

    @Override
    public void flip() {
        FrameBuffer frameBuffer = checkFboExists();

        Viewport screenViewport = viewports.getScreenViewport();
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

    private FrameBuffer checkFboExists() {
        if (frameBuffer == null) {
            throw new IllegalStateException("FrameBuffer doesn't exist; call setWindowSize() first");
        }
        return frameBuffer;
    }

    private void disposeFrameBuffer() {
        frameBuffer = DisposeUtil.dispose(frameBuffer);
    }

    @Override
    public void setWindowSize(IEnvironment env, Dim windowSize) {
        Dim fboSize = vsize;

        // Init framebuffer if needed
        if (frameBuffer == null) {
            frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, fboSize.w, fboSize.h, false);

            GdxViewportUtil.setToOrtho(frameBufferViewport, fboSize, true);
            frameBufferViewport.update(fboSize.w, fboSize.h, true);
        }

        // Update viewports
        Viewport screenViewport = viewports.getScreenViewport();
        GdxViewportUtil.setToOrtho(screenViewport, fboSize, true);
        screenViewport.update(windowSize.w, windowSize.h, true);

        Viewport scene2dViewport = viewports.getScene2dViewport();
        scene2dViewport.update(windowSize.w, windowSize.h, true);

        // Notify others of the changed resolution
        env.updateRenderEnv(Rect.of(0, 0, fboSize.w, fboSize.h), fboSize);
    }

}
