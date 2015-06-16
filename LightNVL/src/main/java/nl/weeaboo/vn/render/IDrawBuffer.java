package nl.weeaboo.vn.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.math.Matrix;

public interface IDrawBuffer {

	public void reset();

	/**
	 * Reserves a range of unique layer identifiers. The returned identifiers are invalidated after every call to {@link #reset()}.
	 */
	public int reserveLayerIds(int count);

	/**
	 * Switches the active layer to {@code layer}. All draw commands submitted from this point onward will be
	 * associated with that layer.
	 */
	public void startLayer(int layerId, ILayer layer);

	public void draw(Entity e);

	public void drawWithTexture(Entity e, ITexture tex);

	public void drawQuad(short z, boolean clipEnabled, BlendMode blendMode, int argb, ITexture tex,
			Matrix transform, Area2D bounds, Area2D uv);

	public void drawLayer(int layerId, ILayer layer);

	/**
	 * Schedules a screenshot to be taken during rendering.
	 *
	 * @param ss The screenshot object to fill with pixels.
	 * @param clip If <code>true</code>, takes a screenshot of just the current
	 *        clipped area. Otherwise, takes a screenshot of the entire render
	 *        area.
	 */
	public void screenshot(IWritableScreenshot ss, boolean clip);

}
