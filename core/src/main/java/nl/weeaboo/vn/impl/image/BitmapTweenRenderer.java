package nl.weeaboo.vn.impl.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.IBitmapTweenRenderer;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.AlignUtil;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.impl.render.TriangleGrid;
import nl.weeaboo.vn.impl.render.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.impl.render.TriangleGrid.TriangleGridLayer;
import nl.weeaboo.vn.impl.scene.AnimatedRenderable;

public abstract class BitmapTweenRenderer extends AnimatedRenderable implements IBitmapTweenRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(BitmapTweenRenderer.class);

    protected final IImageModule imageModule;

    protected final BitmapTweenConfig config;

    // --- Initialized in prepare() ---
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

    protected void disposeRemapTexture() {
        remapTexture = null;
    }

    @Override
    protected void prepareResources() {
        super.prepareResources();

        double width = getWidth();
        double height = getHeight();

        ControlImage controlImage = config.getControlImage();

        // Create geometry
        AlignedTexture tex0 = config.getStartTexture();
        Rect2D bounds0 = tex0.getBounds();
        TextureWrap wrap0 = TextureWrap.CLAMP;

        AlignedTexture tex1 = config.getEndTexture();
        Rect2D bounds1 = tex1.getBounds();
        TextureWrap wrap1 = TextureWrap.CLAMP;

        Rect2D controlBounds = controlImage.getBounds(bounds0, bounds1);
        Area2D controlTexUV;
        TextureWrap controlWrap;

        ITexture controlTex = controlImage.getTexture();
        Rect2D b = AlignUtil.getAlignedBounds(controlTex, 0, 0);
        Area2D uv = controlImage.getUV();
        if (controlImage.isTile()) {
            controlTexUV = Area2D.of(uv.x, uv.y, uv.w * width / b.w, uv.h * height / b.h);
            controlWrap = TextureWrap.REPEAT_BOTH;
        } else {
            final double w;
            final double h;
            if (width > 0 && b.w > 0 && height > 0 && b.h > 0) {
                double sx = width / b.w;
                double sy = height / b.h;
                if (sx >= sy) {
                    w = uv.w;
                    h = sy / sx * uv.h;
                } else {
                    h = uv.h;
                    w = sx / sy * uv.w;
                }
            } else {
                w = h = 0;
            }
            controlTexUV = Area2D.of(uv.x + (1 - w) / 2, uv.y + (1 - h) / 2, w, h);
            controlWrap = TextureWrap.CLAMP;
        }

        grid = TriangleGrid.layout(
                new TriangleGridLayer(bounds0.toArea2D(), getStartTextureUV(), wrap0),
                new TriangleGridLayer(bounds1.toArea2D(), getEndTextureUV(), wrap1),
                new TriangleGridLayer(controlBounds.toArea2D(), controlTexUV, controlWrap)
        );

        remapTexture = updateRemapTexture(remapTexture); // Init remapTex

        LOG.debug("Prepare bitmaptween: start={}, end={}, control={}, bounds={}",
                getStartTexture(), getEndTexture(), controlTex, controlBounds);
    }

    @Override
    protected void updateResources() {
        super.updateResources();

        remapTexture = updateRemapTexture(remapTexture);
    }

    protected abstract ITexture updateRemapTexture(ITexture remapTexture);

    protected TriangleGrid getGeometry() {
        return grid;
    }

    protected ITexture getStartTexture() {
        return config.getStartTexture().getTexture();
    }

    protected Area2D getStartTextureUV() {
        return config.getStartTexture().getUV();
    }

    protected ITexture getEndTexture() {
        return config.getEndTexture().getTexture();
    }

    protected Area2D getEndTextureUV() {
        return config.getEndTexture().getUV();
    }

    protected ITexture getControlTexture() {
        return config.getControlImage().getTexture();
    }

    protected ITexture getRemapTexture() {
        return remapTexture;
    }

    private Rect2D getControlBounds() {
        AlignedTexture start = config.getStartTexture();
        AlignedTexture end = config.getEndTexture();
        return config.getControlImage().getBounds(start.getBounds(), end.getBounds());
    }

    @Override
    public double getNativeWidth() {
        return getControlBounds().w;
    }

    @Override
    public double getNativeHeight() {
        return getControlBounds().h;
    }

}
