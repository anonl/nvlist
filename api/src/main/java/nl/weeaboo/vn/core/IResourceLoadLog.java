package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IResourceLoadLog extends Serializable {

    /**
     * Callback for resource load events.
     *
     * @param resourceId The resource identifier.
     * @param info Additional information concerning the way the resource load was triggered.
     */
    void logLoad(ResourceId resourceId, ResourceLoadInfo info);

}
