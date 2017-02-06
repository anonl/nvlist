package nl.weeaboo.vn.core;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.io.Filenames;

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
     * Creates a copy of this object, but with the given path instead.
     */
    public ResourceLoadInfo withPath(FilePath path) {
        return new ResourceLoadInfo(path, callStackTrace);
    }

    /**
     * Creates a copy of this object, but with an additional suffix appended to the file path.
     * For example: {@code test.png#one -> testmysuffix.png#one}
     */
    public ResourceLoadInfo withFileSuffix(String suffix) {
        FilePath filePath = ResourceId.extractFilePath(path.toString());

        String name = Filenames.stripExtension(filePath.toString()) + suffix;
        String ext = filePath.getExt();
        if (!Strings.isNullOrEmpty(ext)) {
            name += ext;
        }

        return withPath(ResourceId.toResourcePath(FilePath.of(name), getSubId()));
    }

    /**
     * Creates a copy of this object, but with a different sub-resource ID instead.
     * For example: {@code test.png#one -> test.png#two}
     */
    public ResourceLoadInfo withSubId(String subId) {
        FilePath filePath = ResourceId.extractFilePath(path.toString());
        return withPath(ResourceId.toResourcePath(filePath, subId));
    }

    // TODO: This behvaior is inconsistent with withFileSuffix()
    /**
     * Creates a copy of this object, but appending an additional suffix to the sub-resource ID.
     * For example: {@code test.png#one -> test.png#one-two}
     */
    public ResourceLoadInfo withAppendedSubId(String suffix) {
        String current = getSubId();
        if (current.length() == 0) {
            return withSubId(suffix);
        } else if (current.endsWith("-")) {
            return withSubId(current + suffix);
        } else {
            return withSubId(current + "-" + suffix);
        }
    }

    /**
     * @return The relative path to the resource file, including the sub-resource ID.
     *         For example: {@code test.png#one}
     */
    public FilePath getPath() {
        return path;
    }

    /**
     * @return The sub-resource ID, or an empty string if there is no sub-resource ID.
     */
    public String getSubId() {
        return ResourceId.extractSubId(path.getName());
    }

    /**
     * @return If available, a read-only view of the call-stack from where this resource load originated. If not
     *         available, an empty collection is returned.
     */
    public List<String> getCallStackTrace() {
        return callStackTrace;
    }

    @Override
    public String toString() {
        return path.toString();
    }

}
