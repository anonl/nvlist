package nl.weeaboo.vn.image.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
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
        Area2D uv = controlImage.getUV();
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

        remapTexture = updateRemapTexture(remapTexture); // Init remapTex
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

    @Override
    public double getNativeWidth() {
        return getControlTexture().getWidth();
    }

    @Override
    public double getNativeHeight() {
        return getControlTexture().getHeight();
    }

}
