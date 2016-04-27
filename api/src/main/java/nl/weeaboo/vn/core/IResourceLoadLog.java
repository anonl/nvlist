package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IResourceLoadLog extends Serializable {

    boolean hasSeen(ResourceId resourceId);

    void logLoad(ResourceId resourceId, ResourceLoadInfo info);

}
