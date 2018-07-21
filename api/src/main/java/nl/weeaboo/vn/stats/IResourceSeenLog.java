package nl.weeaboo.vn.stats;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;

public interface IResourceSeenLog {

    /**
     * @param type The resource type (image, video, etc.)
     * @return {@code true} if {@link #markSeen(ResourceId)} was previously called for this resource.
     */
    boolean hasSeen(MediaType type, FilePath filename);

    /**
     * @return {@code true} if {@link #markSeen(ResourceId)} was previously called for this resource.
     */
    boolean hasSeen(ResourceId resourceId);

    /**
     * Marks a resources as 'seen' by the user.
     */
    boolean markSeen(ResourceId resourceId);

}
