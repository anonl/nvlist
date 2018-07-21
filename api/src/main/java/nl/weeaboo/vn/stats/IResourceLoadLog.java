package nl.weeaboo.vn.stats;

import java.io.Serializable;

import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public interface IResourceLoadLog extends Serializable {

    /**
     * Callback for resource load events.
     *
     * @param resourceId The resource identifier.
     * @param info Additional information concerning the way the resource load was triggered.
     */
    void logLoad(ResourceId resourceId, ResourceLoadInfo info);

}
