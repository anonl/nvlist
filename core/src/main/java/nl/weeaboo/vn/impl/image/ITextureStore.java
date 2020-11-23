package nl.weeaboo.vn.impl.image;

import javax.annotation.CheckForNull;

import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.IPreloadHandler;

public interface ITextureStore extends IPreloadHandler {

    /**
     * Clears all cached resources.
     */
    void clear();

    /**
     * Returns the texture with the given identifier (loading it if needed). Returns {@code null} if no such
     * texture could be loaded.
     */
    @CheckForNull
    ITexture getTexture(ResourceId id);

    /**
     * Returns a solid-color texture.
     * @param argb ARGB8888, unassociated alpha
     */
    ITexture getColorTexture(int argb);

}
