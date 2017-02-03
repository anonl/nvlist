package nl.weeaboo.vn.impl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ISeenLogHolder;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;

final class ResourceLoadLog implements IResourceLoadLog {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoadLog.class);

    private final ISeenLogHolder seen;

    public ResourceLoadLog(ISeenLogHolder seen) {
        this.seen = Checks.checkNotNull(seen);
    }

    @Override
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
        LOG.trace("Load resource: {}", resourceId);

        seen.getResourceLog().markSeen(resourceId);
    }

}
