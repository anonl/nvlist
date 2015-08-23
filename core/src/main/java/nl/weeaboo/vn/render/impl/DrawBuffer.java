package nl.weeaboo.vn.render.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Sort;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.styledtext.layout.ITextLayout;
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
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.text.ITextPart;
import nl.weeaboo.vn.text.impl.TextPart;

public final class DrawBuffer implements IDrawBuffer {

    private final PartType<ITransformablePart> transformablePart;
    private final PartType<IImagePart> imagePart;
    private final PartType<ITextPart> textPart;

    private final Array<ILayer> layers = Array.of(ILayer.class);
    private final IntArray layerStarts = new IntArray();
    private final Array<BaseRenderCommand> commands = Array.of(BaseRenderCommand.class);

	public DrawBuffer(BasicPartRegistry partRegistry) {
		this.transformablePart = partRegistry.transformable;
		this.imagePart = partRegistry.image;
        this.textPart = partRegistry.text;
	}

	@Override
	public void reset() {
        layers.clear();
        layerStarts.clear();
        commands.clear();
	}

	@Override
    public int reserveLayerIds(int count) {
        int firstId = layers.size;
        for (int n = 0; n < count; n++) {
            layers.add(null);
            layerStarts.add(-1);
        }
        return firstId;
    }

    @Override
	public void startLayer(int layerId, ILayer layer) {
        if (layerId < 0 || layerId >= layers.size) {
			throw new IllegalArgumentException("The given layerId hasn't been reserved yet: " + layerId);
        } else if (layers.get(layerId) == layer) {
			throw new IllegalStateException("Layer has already been added");
		}

        if (layerId == 0 && commands.size == 0) {
			draw(new LayerRenderCommand(layerId, layer));
		}

		layers.set(layerId, layer);
        layerStarts.set(layerId, commands.size);
	}

	@Override
	public void draw(Entity e) {
		IImagePart ip = e.getPart(imagePart);
        if (ip != null) {
            drawWithTexture(e, ip.getTexture());
            return;
        }

        TextPart tp = (TextPart)e.getPart(textPart);
        if (tp != null) {
            tp.draw(this);
        }
	}

	@Override
	public void drawWithTexture(Entity e, ITexture tex) {
        if (tex == null) {
            return;
        }

		final ITransformablePart tp = e.getPart(transformablePart);
		final IDrawablePart dp = tp;
		final IImagePart ip = e.getPart(imagePart);

        double offsetX = AlignUtil.getAlignOffset(tex.getWidth(), tp.getAlignX());
        double offsetY = AlignUtil.getAlignOffset(tex.getHeight(), tp.getAlignY());
        Area2D bounds = Area2D.of(offsetX, offsetY, tp.getUnscaledWidth(), tp.getUnscaledHeight());

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

	public void drawText(short z, boolean clipEnabled, BlendMode blendMode,
	        ITextLayout textLayout, float visibleGlyphs, double x, double y)
	{
	    draw(new TextRenderCommand(z, clipEnabled, blendMode, textLayout, visibleGlyphs, x, y));
    }

	public void draw(BaseRenderCommand cmd) {
        commands.add(cmd);
	}

	public LayerRenderCommand getRootLayerCommand() {
        if (layers.size == 0) {
			return null;
		}
        return (LayerRenderCommand)commands.get(0);
	}

    private int getLayerStart(int layerId) {
        return layerStarts.get(layerId);
	}

    private int getLayerEnd(int layerId) {
        int nextId = layerId + 1;
        if (nextId < layerStarts.size && layerStarts.get(nextId) >= 0) {
            return layerStarts.get(nextId);
        } else {
            return commands.size;
        }
	}

    public List<? extends BaseRenderCommand> getLayerCommands(int layerId) {
        int start = getLayerStart(layerId);
        int end = getLayerEnd(layerId);
        if (end <= start) {
            return Collections.emptyList();
        }
        Sort.instance().sort(commands.items, start, end);
        return Arrays.asList(commands.items).subList(start, end);
    }

}
