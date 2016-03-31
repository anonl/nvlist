package nl.weeaboo.vn.image;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.scene.IRenderable;

public interface ITextureRenderer extends IRenderable {

	/**
	 * Returns the texture used to render this image.
	 */
    ITexture getTexture();

	/**
	 * Returns the UV rectangle used for texture mapping.
	 * @see #setUV(Area2D)
	 */
    Area2D getUV();

	/**
	 * Changes the texture used to render this image.
	 */
    void setTexture(ITexture i);

	/**
	 * Changes the UV width/height used for texture mapping.
	 * @see #setUV(Area2D)
	 */
    void setUV(double w, double h);

	/**
	 * Changes the UV rectangle used for texture mapping.
	 * @see #setUV(Area2D)
	 */
    void setUV(double x, double y, double w, double h);

	/**
	 * Changed the UV rectangle used for texture mapping. The UV-coordinates map
	 * to texture coordinate space, which uses a normalized range from 0 to 1.
	 */
    void setUV(Area2D uv);

    /**
     * Moves the UV rectangle by the specified amount.
     *
     * @see #setUV(double, double, double, double)
     */
    void scrollUV(double du, double dv);

}
