package nl.weeaboo.vn.render.impl;

import java.util.Arrays;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.ITransformablePart;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.image.IImagePart;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IDrawBuffer;

public final class DrawBuffer implements IDrawBuffer {

	private final PartType<? extends ITransformablePart> transformablePart;
	private final PartType<? extends IImagePart> imagePart;

	private ILayer[] layers;
	private int[] layerStarts;
	private int layersL;

	private BaseRenderCommand[] commands;
	private int commandsL;

	public DrawBuffer(BasicPartRegistry partRegistry) {
		this.transformablePart = partRegistry.transformable;
		this.imagePart = partRegistry.image;

		reserveLayers(8);
		reserveCommands(64);
	}

	// === Functions ===========================================================
	private void reserveLayers(int minLength) {
		if (layers != null && layers.length < minLength) {
			return;
		}

		ILayer[] newLayers = new ILayer[Math.max(minLength, layersL*2)];
		int[] newLayerStarts = new int[newLayers.length];
		if (layers != null) {
			System.arraycopy(layers, 0, newLayers, 0, layersL);
		}
		if (layerStarts != null) {
			System.arraycopy(layerStarts, 0, newLayerStarts, 0, layersL);
		}
		layers = newLayers;
		layerStarts = newLayerStarts;
	}

	private void reserveCommands(int minLength) {
		if (commandsL >= minLength) {
			return;
		}

		BaseRenderCommand[] newCommands = new BaseRenderCommand[Math.max(minLength, commandsL*2)];
		if (commands != null) {
			System.arraycopy(commands, 0, newCommands, 0, commandsL);
		}
		commands = newCommands;
	}

	@Override
	public void reset() {
		Arrays.fill(layers, null);
		layersL = 0;

		Arrays.fill(commands, 0, commandsL, null);
		commandsL = 0;
	}

	@Override
	public int reserveLayerIds(int count) {
		reserveLayers(layersL + count);

		int firstId = layersL;
		for (int n = 0; n < count; n++) {
			layerStarts[firstId+n] = 0;
		}
		layersL += count;
		return firstId;
	}

	@Override
	public void startLayer(int layerId, ILayer layer) {
		if (layerId >= layersL) {
			throw new IllegalArgumentException("The given layerId hasn't been reserved yet: " + layerId);
		} else if (layers[layerId] == layer) {
			throw new IllegalStateException("Layer has already been added");
		}

		if (layerId == 0 && commandsL == 0) {
			draw(new LayerRenderCommand(layerId, layer));
		}

		layers[layerId] = layer;
		layerStarts[layerId] = commandsL;
	}

	@Override
	public void draw(Entity e) {
		IImagePart ip = e.getPart(imagePart);
		drawWithTexture(e, ip.getTexture());
	}

	@Override
	public void drawWithTexture(Entity e, ITexture tex) {
		final ITransformablePart tp = e.getPart(transformablePart);
		final IDrawablePart dp = tp;
		final IImagePart ip = e.getPart(imagePart);

		Vec2 offset = AlignUtil.getAlignOffset(tex, tp.getAlignX(), tp.getAlignY());
		Area2D bounds = Area2D.of(offset.x, offset.y, tp.getUnscaledWidth(), tp.getUnscaledHeight());

        drawQuad(dp.getZ(), dp.isClipEnabled(), dp.getBlendMode(), dp.getColorARGB(),
                tex, tp.getTransform(), bounds, ip.getUV());
//            draw(new FadeQuadCommand(dp.getZ(), dp.isClipEnabled(), dp.getBlendMode(), dp.getColorARGB(),
//                    tex, tp.getTransform(), bounds, ip.getUV(), 6, true, 0.5, 0.5));
	}

	@Override
	public void drawQuad(short z, boolean clipEnabled, BlendMode blendMode, int argb,
			ITexture tex, Matrix trans, Area2D bounds, Area2D uv)
	{
		draw(new QuadRenderCommand(z, clipEnabled, blendMode, argb, tex, trans, bounds, uv));
	}

	@Override
	public void screenshot(IWritableScreenshot ss, boolean clip) {
		draw(new ScreenshotRenderCommand(ss, clip));
	}

	@Override
	public void drawLayer(int layerId, ILayer layer) {
		draw(new LayerRenderCommand(layerId, layer));
	}

	public void draw(BaseRenderCommand cmd) {
		reserveCommands(commandsL+1);
		commands[commandsL++] = cmd;
	}

	public BaseRenderCommand[] sortCommands(int start, int end) {
		Arrays.sort(commands, start, end);
		return commands;
	}

	// === Getters =============================================================

	public LayerRenderCommand getRootLayerCommand() {
		if (layersL == 0) {
			return null;
		}
		return (LayerRenderCommand)commands[0];
	}

	public int getLayerStart(int layerId) {
		return layerStarts[layerId];
	}

	public int getLayerEnd(int layerId) {
		return (layerId+1 < layersL ? layerStarts[layerId+1] : commandsL);
	}

	// === Setters =============================================================

}
