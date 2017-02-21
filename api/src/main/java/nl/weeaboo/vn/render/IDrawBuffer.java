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
     * Reserves a range of unique layer identifiers. The returned identifiers are invalidated after every call
     * to {@link #reset()}.
     */
    int reserveLayerIds(int count);

    /**
     * Switches the active layer to {@code layer}. All draw commands submitted from this point onward will be
     * associated with that layer.
     * @param bounds TODO
     */
    void startLayer(int layerId, short z, Rect2D bounds);

    /**
     * Adds a command to draw a textured rectangle to the draw buffer.
     */
    void drawQuad(IDrawTransform transform, int argb, ITexture tex, Area2D bounds, Area2D uv);

    /**
     * Adds a command to draw some text to the draw buffer.
     */
    void drawText(IDrawTransform transform, double dx, double dy, ITextLayout textLayout, double visibleGlyphs);

    /**
     * Adds a command to draw a layer to the draw buffer.
     * @param layerBounds TODO
     * @see #startLayer(int, short, Rect2D)
     */
    void drawLayer(int layerId, short z, Rect2D layerBounds);

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
