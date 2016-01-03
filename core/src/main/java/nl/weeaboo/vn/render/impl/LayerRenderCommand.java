package nl.weeaboo.vn.render.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.scene.ILayer;

public final class LayerRenderCommand extends BaseRenderCommand {

	public static final byte ID = ID_LAYER_RENDER_COMMAND;

	public final int layerId;
	public final Rect2D layerBounds;

	protected LayerRenderCommand(int layerId, ILayer layer) {
		super(ID, layer.getZ(), true, (byte)layerId);

		this.layerId = layerId;
		this.layerBounds = layer.getBounds();
	}

}
