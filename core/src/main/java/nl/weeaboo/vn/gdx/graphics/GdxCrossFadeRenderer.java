package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.image.CrossFadeConfig;
import nl.weeaboo.vn.impl.image.CrossFadeRenderer;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.render.DrawTransform;
import nl.weeaboo.vn.impl.render.GLScreenRenderer;
import nl.weeaboo.vn.impl.render.TriangleGrid;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.scene.IDrawable;

public class GdxCrossFadeRenderer extends CrossFadeRenderer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GdxCrossFadeRenderer.class);

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final IImageModule imageModule;

    private transient ShaderProgram shader;

    public GdxCrossFadeRenderer(IImageModule imageModule, CrossFadeConfig config) {
        super(config);

        this.imageModule = imageModule;
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
            // TODO: If loading fails, this causes an exception every frame.
            // TODO: Standardize some helper for shader loading and use in this class and bitmaptween.
            String filename = "crossfade";
            try {
                shader = shaderStore.get().createShaderFromClasspath(GdxBitmapTweenRenderer.class, filename);
            } catch (IOException e) {
                LOG.warn("Unable to create shader: {}", filename, e);
            }
        }
        return shader;
    }

    private ITexture getBlankTexture() {
        return imageModule.getColorTexture(0x00000000);
    }

    @Override
    protected void renderIntermediate(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        DrawTransform transform = new DrawTransform(parent);
        MutableMatrix matrix = transform.getTransform().mutableCopy();
        matrix.translate(bounds.x, bounds.y);
        matrix.scale(getWidth() / getNativeWidth(), getHeight() / getNativeHeight());
        transform.setTransform(matrix.immutableCopy());

        Logic logic = new Logic(getGeometry(), (float)getNormalizedTime());
        drawBuffer.drawCustom(transform, parent.getColorARGB(), logic);
    }

    protected final class Logic implements IRenderLogic {

        private final TriangleGrid geometry;
        private final float normalizedTime;

        public Logic(TriangleGrid geometry, float normalizedTime) {
            this.geometry = geometry;
            this.normalizedTime = normalizedTime;
        }

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

            LOG.trace("CrossFade[start={}, end={}, time={}]", startTexture, endTexture, normalizedTime);

            shader.begin();
            try {
                // Must init textures in reverse order for some reason (maybe active unit must be 0?)
                GdxShaderUtil.setTexture(shader, 1, endTexture, "u_tex1");
                GdxShaderUtil.setTexture(shader, 0, startTexture, "u_tex0");
                shader.setUniformf("alpha", normalizedTime);

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
