package nl.weeaboo.vn.core;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;

/**
 * Contains information about a callsite of a resource load operation.
 */
public final class ResourceLoadInfo {

    private final String filename;
    private final ImmutableList<String> callStackTrace;

    public ResourceLoadInfo(String filename) {
        this(filename, Collections.<String> emptyList());
    }

    public ResourceLoadInfo(String filename, List<String> callStackTrace) {
        this.filename = Checks.checkNotNull(filename);
        this.callStackTrace = ImmutableList.copyOf(Checks.checkNotNull(callStackTrace));
    }

    /**
     * @return The unnormalized filename.
     */
    public String getFilename() {
        return filename;
    }

    public List<String> getCallStackTrace() {
        return callStackTrace;
    }

}
