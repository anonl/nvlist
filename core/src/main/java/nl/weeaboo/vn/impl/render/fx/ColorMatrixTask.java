package nl.weeaboo.vn.impl.render.fx;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.render.OffscreenRenderTask;

/**
 * Generates an output texture by applying a color matrix to the input.
 */
public final class ColorMatrixTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final ITexture tex;
    private final ColorMatrix matrix;

    public ColorMatrixTask(IImageModule imageModule, ITexture tex, ColorMatrix m) {
        super(imageModule, tex);

        this.tex = tex;
        this.matrix = m;
    }

    @Override
    protected Pixmap render(RenderContext context) throws IOException {
        TextureRegion texRegion = GdxTextureUtil.getTextureRegion(tex);
        if (texRegion == null) {
            throw new IOException("Backing texture unexpectedly missing: " + tex);
        }

        ShaderProgram shader = null;
        PingPongFbo fbos = null;
        try {
            fbos = new PingPongFbo(context.outerSize);
            fbos.start();

            shader = shaderStore.get().createShaderFromClasspath(getClass(), "colormatrix");

            shader.bind();
            shader.setUniformMatrix4fv("u_matrix", matrix.getGLMatrix(), 0, 16);
            shader.setUniform4fv("u_offset", matrix.getGLOffset(), 0, 4);
            context.drawInitial(texRegion, shader);

            return fbos.stop();
        } finally {
            DisposeUtil.dispose(shader);
            DisposeUtil.dispose(fbos);
        }
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("%s[matrix=%s]",
                getClass().getSimpleName(), matrix);
    }

}
