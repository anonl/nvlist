package nl.weeaboo.vn.impl.stats;

import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.stats.IResourceLoadLog;

public class ResourceLoadLogStub implements IResourceLoadLog {

    private static final long serialVersionUID = 1L;

    @Override
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
    }

}
