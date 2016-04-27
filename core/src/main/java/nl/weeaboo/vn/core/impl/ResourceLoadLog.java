package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;

final class ResourceLoadLog implements IResourceLoadLog {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoadLog.class);

    @Override
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
        LOG.trace("Load resource: {}", resourceId);
    }

    @Override
    public boolean hasSeen(ResourceId resourceId) {
        return false;
    }

}
