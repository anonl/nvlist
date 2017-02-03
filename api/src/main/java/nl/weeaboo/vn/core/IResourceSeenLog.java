package nl.weeaboo.vn.core;

import nl.weeaboo.filesystem.FilePath;

public interface IResourceSeenLog {

    boolean hasSeen(MediaType type, FilePath filename);

    boolean hasSeen(ResourceId resourceId);

    boolean markSeen(ResourceId resourceId);

}
