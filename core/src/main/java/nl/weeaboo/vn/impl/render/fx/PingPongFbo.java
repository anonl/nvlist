package nl.weeaboo.vn.impl.render.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.gdx.graphics.GdxScreenshotUtil;
import nl.weeaboo.vn.gdx.res.DisposeUtil;

final class PingPongFbo implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(PingPongFbo.class);

    private final Dim size;

    private FrameBuffer[] fbos = new FrameBuffer[2];
    private int currentIndex = -1;

    public PingPongFbo(Dim size) {
        if (size.w <= 0 || size.h <= 0) {
            throw new IllegalArgumentException("Both dimensions must be > 0, was: " + size);
        }

        this.size = size;
    }

    public void start() {
        currentIndex = 0;

        currentFbo().begin();
        glClear();
    }

    private void glClear() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public TextureRegion next() {
        FrameBuffer prev = currentFbo();
        prev.end();

        Texture tex = prev.getColorBufferTexture();

        currentIndex = (currentIndex + 1) % fbos.length;

        FrameBuffer next = currentFbo();
        next.begin();

        return new TextureRegion(tex);
    }

    public TextureRegion nextBlank() {
        TextureRegion result = next();

        glClear();

        return result;
    }

    public Pixmap stop() {
        // Read back results (slow and blocking)
        Pixmap pixmap = GdxScreenshotUtil.screenshot(Rect.of(0, 0, size.w, size.h));

        currentFbo().end();

        return pixmap;
    }

    private FrameBuffer currentFbo() {
        FrameBuffer result = fbos[currentIndex];
        if (result == null) {
            final Format format = Format.RGBA8888;

            LOG.info("Create FBO: size={}x{}, format={}", size.w, size.h, format);

            result = new FrameBuffer(format, size.w, size.h, false);
            fbos[currentIndex] = result;
        }
        return result;
    }

    @Override
    public void dispose() {
        for (int n = 0; n < fbos.length; n++) {
            fbos[n] = DisposeUtil.dispose(fbos[n]);
        }
    }

}
