package nl.weeaboo.vn.impl.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.stats.IResourceLoadLog;
import nl.weeaboo.vn.stats.ISeenLogHolder;

final class ResourceLoadLog implements IResourceLoadLog {

    private static final long serialVersionUID = StatsImpl.serialVersionUID;
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
