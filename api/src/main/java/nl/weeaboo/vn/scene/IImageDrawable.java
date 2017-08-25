package nl.weeaboo.vn.scene;

import javax.annotation.Nullable;

import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureRenderer;

public interface IImageDrawable extends ITransformable {

    /**
     * @return The renderer that this drawable uses to draw itself.
     */
    IRenderable getRenderer();

    /**
     * Changes the renderer. Calls {@link #setRenderer(IRenderable, Direction)} with an achor of
     * {@link Direction#TOP_LEFT}.
     */
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
    @Nullable ITexture getTexture();

    /**
     * Sets the renderer to a {@link ITextureRenderer} using the given texture
     */
    void setTexture(ITexture texture);

    /**
     * @see #setTexture(ITexture, Direction)
     */
    void setTexture(ITexture texture, int anchor);

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
