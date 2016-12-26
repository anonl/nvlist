package nl.weeaboo.vn.image.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.image.ICrossFadeRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.impl.TriangleGrid;
import nl.weeaboo.vn.render.impl.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.scene.impl.AnimatedRenderable;

public abstract class CrossFadeRenderer extends AnimatedRenderable implements ICrossFadeRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(CrossFadeRenderer.class);

    protected final CrossFadeConfig config;

    // --- Initialized in prepare() ---
    private TriangleGrid grid;

    public CrossFadeRenderer(CrossFadeConfig config) {
        super(config.getDuration());

        this.config = config;
    }

    @Override
    protected void disposeResources() {
        super.disposeResources();

        grid = null;
    }

    @Override
    protected void prepareResources() {
        super.prepareResources();

        AlignedTexture tex0 = config.getStartTexture();
        AlignedTexture tex1 = config.getEndTexture();

        grid = TriangleGrid.layout2(
                tex0.getBounds().toArea2D(), tex0.getUV(), TextureWrap.CLAMP,
                tex1.getBounds().toArea2D(), tex1.getUV(), TextureWrap.CLAMP);

        LOG.debug("Prepare crossFade: start={}, end={}",
                getStartTexture(), getEndTexture());
    }

    @Override
    public double getNativeWidth() {
        return config.getBounds().w;
    }

    @Override
    public double getNativeHeight() {
        return config.getBounds().h;
    }

    protected TriangleGrid getGeometry() {
        return grid;
    }

    protected ITexture getStartTexture() {
        return config.getStartTexture().getTexture();
    }

    protected ITexture getEndTexture() {
        return config.getEndTexture().getTexture();
    }

}
