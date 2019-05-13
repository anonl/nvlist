package nl.weeaboo.vn.impl.render.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.gdx.graphics.GLBlendMode;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.impl.render.OffscreenRenderTask;
import nl.weeaboo.vn.impl.render.fx.ImageCompositeConfig.TextureEntry;

public final class ImageCompositeTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ImageCompositeTask.class);

    private final ImageCompositeConfig config;

    public ImageCompositeTask(IImageModule imageModule, ImageCompositeConfig config) {
        super(imageModule, config.getSize());

        this.config = config;
    }

    @Override
    protected Pixmap render(RenderContext context) {
        Dim outerSize = context.outerSize;
        if (outerSize.w <= 0 || outerSize.h <= 0) {
            LOG.info("Skip {}, outerSize is empty: {}", getClass().getSimpleName(), outerSize);
            return PixmapUtil.newUninitializedPixmap(outerSize.w, outerSize.h, Format.RGBA8888);
        }

        PingPongFbo fbos = null;
        try {
            fbos = new PingPongFbo(outerSize);
            fbos.start();

            SpriteBatch batch = context.batch;
            batch.begin();
            try {
                for (TextureEntry entry : config.getEntries()) {
                    TextureRegion region = GdxTextureUtil.getTextureRegion(entry.getTexture());
                    Rect2D r = entry.getBounds();
                    GLBlendMode.from(entry.getBlendMode()).apply(batch);
                    batch.draw(region, (float)r.x, (float)r.y, (float)r.w, (float)r.h);
                }
            } finally {
                batch.end();
            }

            return fbos.stop();
        } finally {
            DisposeUtil.dispose(fbos);
        }
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("%s[config=%s]",
                getClass().getSimpleName(), config);
    }

}
