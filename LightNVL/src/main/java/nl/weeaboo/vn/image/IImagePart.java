package nl.weeaboo.vn.image;

import nl.weeaboo.common.Area2D;

public interface IImagePart {

	/**
	 * Returns the texture used to render this image.
	 */
	public ITexture getTexture();

	/**
	 * Returns the UV rectangle used for texture mapping.
	 * @see #setUV(Area2D)
	 */
	public Area2D getUV();

	/**
	 * Changes the texture used to render this image.
	 * @see #setTexture(ITexture, double, double)
	 */
	public void setTexture(ITexture i);

	/**
	 * Changes the texture used to render this image.
	 * @param anchor If the new texture is a different size than the old one,
	 *        the anchor determines the relative position of the new texture.
	 *        The anchor values align in the direction of a standard keyboard
	 *        numpad (5=center, 7=top-left, etc).
	 * @see #setTexture(ITexture, double, double)
	 */
	public void setTexture(ITexture i, int anchor);

	/**
	 * Changes the texture used to render this image.
	 *
	 */
	public void setTexture(ITexture i, double imageAlignX, double imageAlignY);

	/**
	 * Changes the UV width/height used for texture mapping.
	 * @see #setUV(Area2D)
	 */
	public void setUV(double w, double h);

	/**
	 * Changes the UV rectangle used for texture mapping.
	 * @see #setUV(Area2D)
	 */
	public void setUV(double x, double y, double w, double h);

	/**
	 * Changed the UV rectangle used for texture mapping. The UV-coordinates map
	 * to texture coordinate space, which uses a normalized range from 0 to 1.
	 */
	public void setUV(Area2D uv);

}
