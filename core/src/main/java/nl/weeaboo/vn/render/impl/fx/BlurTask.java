package nl.weeaboo.vn.render.impl.fx;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.res.DisposeUtil;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.impl.OffscreenRenderTask;

public final class BlurTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final ITexture tex;
    private final int radius;

    public BlurTask(IImageModule imageModule, ITexture tex, int radius) {
        super(imageModule, tex);

        this.tex = tex;
        this.radius = radius;

        setSize(Dim.of(tex.getPixelWidth() / 2, tex.getPixelHeight() / 2));
        // TODO: Scale relative to current scale, don't set absolute values
        setResultScale(2.0, 2.0);
    }

    @Override
    protected Pixmap render(RenderContext context) throws IOException {
        Dim size = context.size;

        Vec2 rad = toTextureCoords(radius);

        ShaderProgram shader = null;
        PingPongFbo fbos = null;
        try {
            fbos = new PingPongFbo(size);
            fbos.start();

            context.draw(GdxTextureUtil.getTextureRegion(tex), null);

            shader = shaderStore.get().createShaderFromClasspath(getClass(), "blur");
            shader.begin();

            // Horizontal
            shader.setUniformf("radius", (float)rad.x, 0);
            context.draw(fbos.nextBlank(), shader);

            // Vertical
            shader.begin();
            shader.setUniformf("radius", 0, (float)rad.y);
            context.draw(fbos.nextBlank(), shader);

            return fbos.stop();
        } finally {
            DisposeUtil.dispose(shader);
            DisposeUtil.dispose(fbos);
        }
    }

    private Vec2 toTextureCoords(double distance) {
        return new Vec2(distance / tex.getWidth(), distance / tex.getHeight());
    }

}
