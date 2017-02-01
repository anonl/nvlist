package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig;
import nl.weeaboo.vn.impl.image.BitmapTweenRenderer;
import nl.weeaboo.vn.impl.image.PixelTextureData;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.render.DrawTransform;
import nl.weeaboo.vn.impl.render.GLScreenRenderer;
import nl.weeaboo.vn.impl.render.TriangleGrid;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.scene.IDrawable;

public final class GdxBitmapTweenRenderer extends BitmapTweenRenderer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GdxBitmapTweenRenderer.class);

    private static final int INTERPOLATOR_MAX = 255;

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

        drawBuffer.drawCustom(transform, parent.getColorARGB(), new Logic());
    }

    // TODO: Re-enable optimized versions of renderStart/renderEnd
    /*
    @Override
    protected void renderStart(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        ITexture tex = getStartTexture();
        if (tex != null) {
            drawBuffer.drawQuad(parent, parent.getColorARGB(), tex, bounds, getStartTextureUV());
        }
    }

    @Override
    protected void renderEnd(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        ITexture tex = getEndTexture();
        if (tex != null) {
            drawBuffer.drawQuad(parent, parent.getColorARGB(), tex, bounds, getEndTextureUV());
        }
    }
    */

    @Override
    protected ITexture updateRemapTexture(ITexture remapTexture) {
        double i1 = INTERPOLATOR_MAX * getNormalizedTime() * (1 + config.getRange());
        double i0 = i1 - INTERPOLATOR_MAX * config.getRange();

        // Create remap texture
        int w = INTERPOLATOR_MAX + 1;
        int h = 1;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.Intensity);
        try {
            ByteBuffer buf = pixmap.getPixels();

            IInterpolator interpolator = config.getInterpolator();
            for (int n = 0; n < w * h; n++) {
                byte value;
                if (n <= i0) {
                    // Fully visible
                    value = (byte)INTERPOLATOR_MAX;
                } else if (n >= i1) {
                    // Not visible yet
                    value = (byte)0;
                } else {
                    // Value between i0 and i1; partially visible
                    // f is 1.0 at i0, 0.0 at i1
                    double f = (i1 - n) / (i1 - i0);
                    float val = INTERPOLATOR_MAX * interpolator.remap((float)f);
                    value = (byte)Math.max(0, Math.min(INTERPOLATOR_MAX, val));
                }
                buf.put(n, value);
            }

            Texture backingTexture = GdxTextureUtil.getTexture(remapTexture);

            LOG.trace("Remap tex: {}->{} ({})", i0, i1, (backingTexture == null ? "alloc" : "update"));

            if (backingTexture != null) {
                // Update existing texture
                backingTexture.draw(pixmap, 0, 0);
                pixmap.dispose();
            } else {
                // (Re)allocate texture
                disposeRemapTexture();
                PixelTextureData texData = PixelTextureData.fromPremultipliedPixmap(pixmap);
                remapTexture = imageModule.createTexture(texData, 1, 1);
            }
        } catch (RuntimeException re) {
            pixmap.dispose();
            throw re;
        }

        return remapTexture;
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
