package nl.weeaboo.vn.render;


public interface IRenderer<D extends IDrawBuffer> {

	/**
	 * Renders all buffered draw commands.
	 * @param buffer A draw buffer containing the draw commands to render.
	 */
	public void render(D buffer);

}
