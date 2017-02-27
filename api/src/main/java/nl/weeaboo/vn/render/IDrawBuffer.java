package nl.weeaboo.vn.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;

public interface IDrawBuffer {

    /**
     * Resets the draw buffer, clearing all stored data.
     */
    void reset();

    /**
     * Adds a command to draw a textured rectangle to the draw buffer.
     */
    void drawQuad(IDrawTransform transform, int argb, ITexture tex, Area2D bounds, Area2D uv);

    /**
     * Adds a command to draw some text to the draw buffer.
     */
    void drawText(IDrawTransform transform, double dx, double dy, ITextLayout textLayout, double visibleGlyphs);

    /**
     * Returns a new draw buffer that can be used to draw the contents of a sub-layer.
     */
    IDrawBuffer subLayerBuffer(short layerZ, Rect2D layerBounds, double contentDx, double contentDy);

    /**
     * Adds a custom render command to the draw buffer.
     */
    void drawCustom(IDrawTransform transform, int argb, IRenderLogic renderLogic);

    /**
     * Schedules a screenshot to be taken during rendering.
     *
     * @param ss The screenshot object to fill with pixels.
     * @param clip If {@code true}, takes a screenshot of just the current clipped area. Otherwise, takes a screenshot
     *        of the entire render area.
     */
    void screenshot(IWritableScreenshot ss, boolean clip);

}
