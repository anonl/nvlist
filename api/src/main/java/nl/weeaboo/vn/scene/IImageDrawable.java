package nl.weeaboo.vn.scene;

import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureRenderer;

public interface IImageDrawable extends ITransformable {

    IRenderable getRenderer();

    void setRenderer(IRenderable r); // Calls setRenderer(IRenderable, int)

    /**
     * Changes the renderer, aligning the new renderer relative to the bounds of the previous renderer
     * according to the {@code anchor} param.
     */
    void setRenderer(IRenderable r, Direction anchor); // Calls setRenderer(IRenderable, double, double)

    /**
     * Changes the renderer and its relative alignment.
     *
     * @see #setRenderer(IRenderable)
     * @see #setAlign(double, double)
     */
    void setRenderer(IRenderable r, double alignX, double alignY);

    /**
     * If this image drawable uses a {@link ITextureRenderer}, returns the current texture of that renderer.
     * Otherwise, {@code null} is returned.
     * @see ITextureRenderer#getTexture()
     */
    ITexture getTexture();

    /**
     * Sets the renderer to a {@link ITextureRenderer} using the given texture
     */
    void setTexture(ITexture texture);

    /**
     * @see #setTexture(ITexture)
     * @see #setRenderer(IRenderable, Direction)
     */
    void setTexture(ITexture texture, Direction anchor);

    /**
     * @see #setTexture(ITexture)
     * @see #setRenderer(IRenderable, double, double)
     */
    void setTexture(ITexture texture, double alignX, double alignY);

}
