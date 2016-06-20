package nl.weeaboo.vn.image.impl;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig.InputTexture;
import nl.weeaboo.vn.render.impl.TriangleGrid;
import nl.weeaboo.vn.render.impl.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.scene.impl.AnimatedRenderable;

public abstract class BitmapTweenRenderer extends AnimatedRenderable {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(BitmapTweenRenderer.class);

    private static final int INTERPOLATOR_MAX = 255;

    protected final IImageModule imageModule;

    private final BitmapTweenConfig config;

    private Area2D baseUV = ITexture.DEFAULT_UV;

    // --- Initialized in prepare() ---
    private Dim remapTextureSize;
    private ITexture remapTexture;
    private TriangleGrid grid;

    public BitmapTweenRenderer(IImageModule imageModule, BitmapTweenConfig config) {
        super(config.getDuration());

        this.imageModule = imageModule;
        this.config = config;
    }

    @Override
    protected void disposeResources() {
        super.disposeResources();

        disposeRemapTexture();
        grid = null;
    }

    private void disposeRemapTexture() {
        remapTexture = null;
    }

    @Override
    protected void prepareResources() {
        super.prepareResources();

        double width = getWidth();
        double height = getHeight();

        ControlImage controlImage = config.getControlImage();

        // Create remap texture
        remapTextureSize = Dim.of(INTERPOLATOR_MAX + 1, 1);

        // Create geometry
        InputTexture tex0 = config.getStartTexture();
        Rect2D bounds0 = tex0.getBounds();
        TextureWrap wrap0 = TextureWrap.CLAMP;

        InputTexture tex1 = config.getEndTexture();
        Rect2D bounds1 = tex1.getBounds();
        TextureWrap wrap1 = TextureWrap.CLAMP;

        Rect2D controlBounds = controlImage.getBounds(bounds0, bounds1);
        Area2D controlTexUV;
        TextureWrap controlWrap;

        ITexture controlTex = controlImage.getTexture();
        Rect2D b = AlignUtil.getAlignedBounds(controlTex, 0, 0);
        Area2D uv = controlTex.getUV();
        if (controlImage.isTile()) {
            controlTexUV = Area2D.of(uv.x, uv.y, uv.w * width / b.w, uv.h * height / b.h);
            controlWrap = TextureWrap.REPEAT_BOTH;
        } else {
            double sx = width / b.w, sy = height / b.h;
            double w, h;
            if (sx >= sy) {
                w = uv.w;
                h = sy / sx * uv.h;
            } else {
                h = uv.h;
                w = sx / sy * uv.w;
            }
            controlTexUV = Area2D.of(uv.x + (1 - w) / 2, uv.y + (1 - h) / 2, w, h);
            controlWrap = TextureWrap.CLAMP;
        }

        grid = TriangleGrid.layout3(
                bounds0.toArea2D(), getStartTextureUV(), wrap0,
                bounds1.toArea2D(), getEndTextureUV(), wrap1,
                controlBounds.toArea2D(), controlTexUV, controlWrap);

        updateRemapTex(); // Init remapTex
    }

    @Override
    protected void updateResources() {
        super.updateResources();

        updateRemapTex();
    }

    private boolean updateRemapTex() {
        double i1 = INTERPOLATOR_MAX * getNormalizedTime() * (1 + config.getRange());
        double i0 = i1 - INTERPOLATOR_MAX * config.getRange();

        int w = remapTextureSize.w;
        int h = remapTextureSize.h;
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

            LOG.debug("Remap tex: {}->{} ({})", i0, i1, (backingTexture == null ? "alloc" : "update"));

            if (backingTexture != null) {
                // Update existing texture
                backingTexture.draw(pixmap, 0, 0);
                pixmap.dispose();
            } else {
                // (Re)allocate texture
                disposeRemapTexture();
                remapTexture = imageModule.createTexture(PixelTextureData.fromPixmap(pixmap), 1, 1);
            }
        } catch (RuntimeException re) {
            pixmap.dispose();
            throw re;
        }

        return true;
    }

    protected TriangleGrid getGeometry() {
        return grid;
    }
    protected ITexture getStartTexture() {
        return config.getStartTexture().getTexture();
    }
    protected Area2D getStartTextureUV() {
        return config.getStartTexture().getUV(baseUV);
    }
    protected ITexture getEndTexture() {
        return config.getEndTexture().getTexture();
    }
    protected Area2D getEndTextureUV() {
        return config.getEndTexture().getUV(baseUV);
    }
    protected ITexture getControlTexture() {
        return config.getControlImage().getTexture();
    }
    protected ITexture getRemapTexture() {
        return remapTexture;
    }

    @Override
    public double getNativeWidth() {
        return getControlTexture().getWidth();
    }

    @Override
    public double getNativeHeight() {
        return getControlTexture().getHeight();
    }

}
