package nl.weeaboo.vn.render;

import nl.weeaboo.vn.core.IDestructible;

public interface IScreenRenderer<D extends IDrawBuffer> extends IDestructible {

	/**
	 * Renders all buffered draw commands.
	 * @param buffer A draw buffer containing the draw commands to render.
	 */
	public void render(D buffer);

}
