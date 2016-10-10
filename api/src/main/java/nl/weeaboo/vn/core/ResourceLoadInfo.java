package nl.weeaboo.vn.core;

import java.util.List;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

/**
 * Contains information about a callsite of a resource load operation.
 */
public final class ResourceLoadInfo {

    private final FilePath path;
    private final ImmutableList<String> callStackTrace;

    public ResourceLoadInfo(FilePath path) {
        this(path, ImmutableList.<String>of());
    }

    public ResourceLoadInfo(FilePath path, List<String> callStackTrace) {
        this.path = Checks.checkNotNull(path);
        this.callStackTrace = ImmutableList.copyOf(Checks.checkNotNull(callStackTrace));
    }

    /**
     * Creates a copy of this resource load info, but with the given path instead.
     */
    public ResourceLoadInfo withPath(FilePath path) {
        return new ResourceLoadInfo(path, callStackTrace);
    }

    public ResourceLoadInfo withFileSuffix(String suffix) {
        FilePath filePath = ResourceId.getFilePath(path.toString());
        return withPath(ResourceId.toResourcePath(FilePath.of(filePath + suffix), getSubId()));
    }

    public ResourceLoadInfo withSubId(String subId) {
        FilePath filePath = ResourceId.getFilePath(path.toString());
        return withPath(ResourceId.toResourcePath(filePath, subId));
    }

    public ResourceLoadInfo withAppendedSubId(String suffix) {
        String current = getSubId();
        if (current.length() == 0) {
            return withSubId(suffix);
        } else {
            return withSubId(current + "-" + suffix);
        }
    }

    public FilePath getPath() {
        return path;
    }

    public String getSubId() {
        return ResourceId.getSubId(path.getName());
    }

    public List<String> getCallStackTrace() {
        return callStackTrace;
    }

    @Override
    public String toString() {
        return path.toString();
    }

}
