package nl.weeaboo.vn.impl.render;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IDrawTransform;
import nl.weeaboo.vn.render.IRenderLogic;

public final class DrawBuffer implements IDrawBuffer {

    private final Array<BaseRenderCommand> contents = Array.of(BaseRenderCommand.class);
    private final Array<DrawBuffer> subLayers = Array.of(DrawBuffer.class);

    @Override
    public void reset() {
        contents.clear();
        subLayers.clear();
    }

    @Override
    public IDrawBuffer subLayerBuffer(short layerZ, Rect2D layerBounds, double contentDx, double contentDy) {
        int layerId = subLayers.size;
        LayerRenderCommand lrc = new LayerRenderCommand(layerId, layerZ, layerBounds, contentDx, contentDy);
        contents.add(lrc);

        DrawBuffer subLayerBuffer = new DrawBuffer();
        subLayers.add(subLayerBuffer);
        return subLayerBuffer;
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
    public void drawText(IDrawTransform dt, double dx, double dy, ITextLayout textLayout,
            double visibleGlyphs) {
        draw(new TextRenderCommand(dt, dx, dy, textLayout, visibleGlyphs));
    }

    @Override
    public void drawCustom(IDrawTransform dt, int argb, IRenderLogic renderLogic) {
        draw(new CustomRenderCommand(dt.getZ(), dt.isClipEnabled(), dt.getBlendMode(), argb,
                dt.getTransform(), renderLogic));
    }

    /** Adds a draw command to the draw buffer. */
    public void draw(BaseRenderCommand cmd) {
        contents.add(cmd);
    }

    /**
     * Returns the render commands for the requested layer, or an empty collection if nothing is stored for that layer
     * (even if the layer doesn't exist).
     */
    public DrawBuffer getLayerBuffer(int layerId) {
        return subLayers.get(layerId);
    }

    /**
     * Returns a sorted list of render commands stored in this buffer.
     */
    public List<? extends BaseRenderCommand> getCommands() {
        Sort.instance().sort(contents.items, 0, contents.size);
        return Arrays.asList(contents.items).subList(0, contents.size);
    }

}
