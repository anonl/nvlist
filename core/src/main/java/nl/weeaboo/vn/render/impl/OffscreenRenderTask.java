package nl.weeaboo.vn.render.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.gdx.graphics.GdxScreenshotUtil;
import nl.weeaboo.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.gdx.res.DisposeUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.PixelTextureData;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IOffscreenRenderTask;

/**
 * Renders to an offscreen buffer (FBO) and reads the result back to a texture.
 */
public abstract class OffscreenRenderTask extends AsyncRenderTask implements IOffscreenRenderTask {

    private static final long serialVersionUID = RenderImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(OffscreenRenderTask.class);

    private final IImageModule imageModule;
    private final Dim size;

    private transient PixelTextureData resultPixels;
    private Vec2 resultScale = new Vec2();

    public OffscreenRenderTask(IImageModule imageModule, Dim size) {
        this.imageModule = imageModule;
        this.size = size;

        isTransient = true;
    }

    public OffscreenRenderTask(IImageModule imageModule, ITexture tex) {
        this(imageModule, Dim.of(tex.getPixelWidth(), tex.getPixelHeight()));

        resultScale.x = tex.getScaleX();
        resultScale.y = tex.getScaleY();
    }

    @Override
    public void cancel() {
        super.cancel();

        resultPixels = null;
    }

    @Override
    public boolean isAvailable() {
        return !isFailed() && resultPixels != null;
    }

    @Override
    public ITexture getResult() {
        if (resultPixels == null) {
            return null;
        }
        return imageModule.createTexture(resultPixels, resultScale.x, resultScale.y);
    }

    @Override
    public final void render() {
        Stopwatch sw = Stopwatch.createStarted();

        RenderContext renderContext = new RenderContext(size);
        try {
            Pixmap pixels;
            renderContext.begin();
            try {
                renderToFbo(renderContext);

                // Read back results (slow and blocking)
                pixels = GdxScreenshotUtil.screenshot(Rect.of(0, 0, size.w, size.h));
            } finally {
                renderContext.end();
            }

            resultPixels = PixelTextureData.fromPremultipliedPixmap(pixels);
        } finally {
            renderContext.dispose();
        }

        LOG.debug("Offscreen render task {} took {}", this, sw);
    }

    protected abstract void renderToFbo(RenderContext context);

    protected void setResultScale(double sx, double sy) {
        resultScale.x = sx;
        resultScale.y = sy;
    }

    protected static final class RenderContext implements Disposable {

        public final Dim size;
        public final FrameBuffer fbo;
        public final SpriteBatch batch;

        protected RenderContext(Dim size) {
            this.size = size;

            batch = new SpriteBatch();
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, size.w, size.h, false);
        }

        public void begin() {
            ScreenViewport viewport = new ScreenViewport();
            GdxViewportUtil.setToOrtho(viewport, size, true);
            viewport.update(size.w, size.h, false);

            batch.setProjectionMatrix(viewport.getCamera().combined);

            fbo.begin();
        }

        public void end() {
            fbo.end();
        }

        @Override
        public void dispose() {
            DisposeUtil.dispose(fbo);
            DisposeUtil.dispose(batch);
        }

    }

}
