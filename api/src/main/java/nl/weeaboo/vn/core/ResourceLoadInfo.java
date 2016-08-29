package nl.weeaboo.vn.core;

import java.util.List;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;

/**
 * Contains information about a callsite of a resource load operation.
 */
public final class ResourceLoadInfo {

    private final String path;
    private final ImmutableList<String> callStackTrace;

    public ResourceLoadInfo(String path) {
        this(path, ImmutableList.<String>of());
    }

    public ResourceLoadInfo(String path, List<String> callStackTrace) {
        this.path = Checks.checkNotNull(path);
        this.callStackTrace = ImmutableList.copyOf(Checks.checkNotNull(callStackTrace));
    }

    /**
     * Creates a copy of this resource load info, but with the given path instead.
     */
    public ResourceLoadInfo withPath(String path) {
        return new ResourceLoadInfo(path, callStackTrace);
    }

    public ResourceLoadInfo withFileSuffix(String suffix) {
        String filePath = ResourceId.getFilePath(path);
        String subId = ResourceId.getSubId(path);
        return withPath(ResourceId.toResourcePath(filePath + suffix, subId));
    }

    public ResourceLoadInfo withSubId(String subId) {
        String filePath = ResourceId.getFilePath(path);
        return withPath(ResourceId.toResourcePath(filePath, subId));
    }

    public String getPath() {
        return path;
    }

    public List<String> getCallStackTrace() {
        return callStackTrace;
    }

}
