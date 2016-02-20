package nl.weeaboo.gdx.graphics;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig;
import nl.weeaboo.vn.image.impl.BitmapTweenRenderer;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.render.impl.GLScreenRenderer;
import nl.weeaboo.vn.render.impl.TriangleGrid;
import nl.weeaboo.vn.scene.IDrawable;

public final class GdxBitmapTweenRenderer extends BitmapTweenRenderer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GdxBitmapTweenRenderer.class);

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private transient ShaderProgram shader;

    public GdxBitmapTweenRenderer(IImageModule imageModule, BitmapTweenConfig config) {
        super(imageModule, config);
    }

    @Override
    protected void prepareResources() {
        super.prepareResources();

        getShader();
    }

    @Override
    protected void disposeResources() {
        super.disposeResources();

        if (shader != null) {
            shader.dispose();
            shader = null;
        }
    }

    private ShaderProgram getShader() {
        if (shader == null) {
            String filename = "bitmaptween";
            try {
                shader = shaderStore.get().createShader(filename);
            } catch (IOException e) {
                LOG.warn("Unable to create shader: {}", filename, e);
            }
        }
        return shader;
    }

    private ITexture getBlankTexture() {
        return imageModule.getTexture("blank");
    }

    @Override
    protected void renderStart(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        ITexture tex = getStartTexture();
        if (tex != null) {
            drawBuffer.drawQuad(parent, parent.getColorARGB(), tex, bounds, getStartTextureUV());
        }
    }

    @Override
    protected void renderIntermediate(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        drawBuffer.drawCustom(parent, parent.getColorARGB(), new Logic());
    }

    @Override
    protected void renderEnd(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        ITexture tex = getEndTexture();
        if (tex != null) {
            drawBuffer.drawQuad(parent, parent.getColorARGB(), tex, bounds, getEndTextureUV());
        }
    }

    protected final class Logic implements IRenderLogic {

        private final TriangleGrid geometry = getGeometry();

        @Override
        public void render(IScreenRenderer<?> renderer) {
            GLScreenRenderer rr = (GLScreenRenderer)renderer;

            ShaderProgram shader = getShader();
            if (shader == null) {
                LOG.debug("Abort bitmap tween render, unable to create shader");
                return;
            }

            ITexture startTexture = getStartTexture();
            if (startTexture == null) {
                startTexture = getBlankTexture();
            }

            ITexture endTexture = getEndTexture();
            if (endTexture == null) {
                endTexture = getBlankTexture();
            }

            ITexture controlTexture = getControlTexture();

            LOG.trace("BitmapTween[start={}, end={}, control={}]", startTexture, endTexture, controlTexture);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GLBlendMode.DEFAULT_PREMULT.srcFunc, GLBlendMode.DEFAULT_PREMULT.dstFunc);

            shader.begin();
            try {
                // Must init textures in reverse order for some reason (maybe active unit must be 0?)
                GdxShaderUtil.setTexture(shader, 3, getRemapTexture(), "u_interpolationLUT");
                GdxShaderUtil.setTexture(shader, 2, controlTexture, "u_controlTex");
                GdxShaderUtil.setTexture(shader, 1, endTexture, "u_tex1");
                GdxShaderUtil.setTexture(shader, 0, startTexture, "u_tex0");

                rr.renderTriangleGrid(geometry, shader);
            } finally {
                shader.end();
            }
            for (int n = 3; n >= 0; n--) {
                GdxTextureUtil.bindTexture(n, null);
            }
        }

    }

}
