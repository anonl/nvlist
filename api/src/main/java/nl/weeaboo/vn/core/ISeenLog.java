package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;

public interface ISeenLog extends Serializable {

    boolean hasSeen(MediaType type, FilePath filename);
    boolean hasSeen(ResourceId resourceId);
    boolean markSeen(ResourceId resourceId);

    void registerScriptFile(ResourceId resourceId, int numTextLines);
    boolean hasSeenLine(FilePath filename, int lineNumber);
    boolean hasSeenLine(ResourceId resourceId, int lineNumber);
    void markLineSeen(FilePath filename, int lineNumber);
    void markLineSeen(ResourceId resourceId, int lineNumber);

    void load(SecureFileWriter sfw, FilePath path) throws IOException;
    void save(SecureFileWriter sfw, FilePath path) throws IOException;

}
