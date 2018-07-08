package nl.weeaboo.vn.impl.core;

import java.io.Serializable;

import nl.weeaboo.vn.core.ResourceId;

/**
 * Implementation of {@link ResourceLoader#preloadNormalized(nl.weeaboo.vn.core.ResourceId)}.
 */
public interface IPreloadHandler extends Serializable {

    /**
     * Request a preload of the given resource.
     *
     * @param resourceId Canonical identifier of the resource to preload.
     */
    void preloadNormalized(ResourceId resourceId);

}
