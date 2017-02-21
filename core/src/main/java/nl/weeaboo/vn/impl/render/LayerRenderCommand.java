package nl.weeaboo.vn.impl.render;

import nl.weeaboo.common.Rect2D;

public final class LayerRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_LAYER_RENDER_COMMAND;

    public final int layerId;
    public final Rect2D layerBounds;

    protected LayerRenderCommand(int layerId, short z, Rect2D layerBounds) {
        super(ID, z, true, (byte)layerId);

        this.layerId = layerId;
        this.layerBounds = layerBounds;
    }

}
