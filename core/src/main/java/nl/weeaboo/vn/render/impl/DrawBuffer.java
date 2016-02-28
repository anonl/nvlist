package nl.weeaboo.vn.render.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Sort;
import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IDrawTransform;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.scene.ILayer;

public final class DrawBuffer implements IDrawBuffer {

    private final Array<ILayer> layers = Array.of(ILayer.class);
    private final IntArray layerStarts = new IntArray();
    private final Array<BaseRenderCommand> commands = Array.of(BaseRenderCommand.class);

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
    public void drawQuad(IDrawTransform dt, int argb, ITexture tex, Area2D bounds, Area2D uv) {
        draw(new QuadRenderCommand(dt.getZ(), dt.isClipEnabled(), dt.getBlendMode(), argb, tex,
                dt.getTransform(), bounds, uv));
	}

	@Override
	public void screenshot(IWritableScreenshot ss, boolean clip) {
		draw(new ScreenshotRenderCommand(ss, clip));
	}

	@Override
	public void drawLayer(int layerId, ILayer layer) {
		draw(new LayerRenderCommand(layerId, layer));
	}

    @Override
    public void drawText(IDrawTransform dt, double dx, double dy, ITextLayout textLayout,
            double visibleGlyphs) {
        draw(new TextRenderCommand(dt, dx, dy, textLayout, visibleGlyphs));
    }

    @Override
    public void drawCustom(IDrawTransform dt, int argb, IRenderLogic renderLogic) {
        draw(new CustomRenderCommand(dt.getZ(), dt.isClipEnabled(), dt.getBlendMode(), argb,
                dt.getTransform(), renderLogic));
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

    public ImmutableList<RenderCommand> getCommands() {
        return ImmutableList.<RenderCommand> copyOf(commands);
    }

}
