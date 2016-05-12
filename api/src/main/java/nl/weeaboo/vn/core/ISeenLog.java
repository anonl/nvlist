package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface ISeenLog extends Serializable {

    boolean hasSeen(MediaType type, String filename);
    boolean hasSeen(ResourceId resourceId);
    boolean markSeen(ResourceId resourceId);

    void registerScriptFile(ResourceId resourceId, int numTextLines);
    boolean hasSeenLine(ResourceId resourceId, int lineNumber);
    void markLineSeen(ResourceId resourceId, int lineNumber);

}
