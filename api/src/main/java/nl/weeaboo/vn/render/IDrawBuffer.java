package nl.weeaboo.vn.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.scene.ILayer;

public interface IDrawBuffer {

    void reset();

    /**
     * Reserves a range of unique layer identifiers. The returned identifiers are invalidated after every call
     * to {@link #reset()}.
     */
    int reserveLayerIds(int count);

    /**
     * Switches the active layer to {@code layer}. All draw commands submitted from this point onward will be
     * associated with that layer.
     */
    void startLayer(int layerId, ILayer layer);

    void drawQuad(IDrawTransform transform, int argb, ITexture tex, Area2D bounds, Area2D uv);

    void drawText(IDrawTransform transform, double dx, double dy, ITextLayout textLayout,
            double visibleGlyphs);

    void drawLayer(int layerId, ILayer layer);

    void drawCustom(IDrawTransform transform, int argb, IRenderLogic renderLogic);

    /**
     * Schedules a screenshot to be taken during rendering.
     *
     * @param ss The screenshot object to fill with pixels.
     * @param clip If {@code true}, takes a screenshot of just the current clipped area. Otherwise, takes a
     *        screenshot of the entire render area.
     */
    void screenshot(IWritableScreenshot ss, boolean clip);

}
