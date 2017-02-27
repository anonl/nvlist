package nl.weeaboo.vn.impl.render;

import nl.weeaboo.common.Rect2D;

public final class LayerRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_LAYER_RENDER_COMMAND;

    public final int layerId;
    public final Rect2D layerBounds;
    public final double contentDx;
    public final double contentDy;

    protected LayerRenderCommand(int layerId, short z, Rect2D layerBounds, double contentDx, double contentDy) {
        super(ID, z, true, (byte)layerId);

        this.layerId = layerId;
        this.layerBounds = layerBounds;
        this.contentDx = contentDx;
        this.contentDy = contentDy;
    }

}
