package nl.weeaboo.vn.core;

import nl.weeaboo.filesystem.FilePath;

public interface IScriptSeenLog {

    void registerScriptFile(ResourceId resourceId, int numTextLines);

    boolean hasSeenLine(FilePath filename, int lineNumber);

    boolean hasSeenLine(ResourceId resourceId, int lineNumber);

    void markLineSeen(FilePath filename, int lineNumber);

    void markLineSeen(ResourceId resourceId, int lineNumber);

}
