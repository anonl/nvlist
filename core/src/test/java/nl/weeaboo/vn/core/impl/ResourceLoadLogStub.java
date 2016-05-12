package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public class ResourceLoadLogStub implements IResourceLoadLog {

    private static final long serialVersionUID = 1L;

    @Override
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
    }

}
