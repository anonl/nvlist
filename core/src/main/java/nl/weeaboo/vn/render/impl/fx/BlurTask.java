package nl.weeaboo.vn.render.impl.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.impl.OffscreenRenderTask;

public final class BlurTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(BlurTask.class);
    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final ITexture tex;
    private final int radius;

    public BlurTask(IImageModule imageModule, ITexture tex, int radius) {
        super(imageModule, tex);

        this.tex = tex;
        this.radius = radius;
    }

    @Override
    protected void renderToFbo(RenderContext context) {
        Dim size = context.size;
        SpriteBatch batch = context.batch;

        Vec2 rad = toTextureCoords(radius);

        // TODO: You really want a multi-pass separable blur
        try {
            ShaderProgram shader = shaderStore.get().createShaderFromClasspath(getClass(), "blur");
            batch.setShader(shader);
            shader.setUniformf("radius", (float)rad.x, (float)rad.y);

            TextureRegion region = GdxTextureUtil.getTextureRegion(tex);

            batch.begin();
            batch.draw(region, 0, 0, size.w, size.h);
            batch.end();
        } catch (Exception e) {
            LOG.error("Error performing blur", e);
            cancel();
            return;
        }
    }

    private Vec2 toTextureCoords(double distance) {
        return new Vec2(distance / tex.getWidth(), distance / tex.getHeight());
    }

}
