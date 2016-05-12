package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IResourceLoadLog extends Serializable {

    void logLoad(ResourceId resourceId, ResourceLoadInfo info);

}
