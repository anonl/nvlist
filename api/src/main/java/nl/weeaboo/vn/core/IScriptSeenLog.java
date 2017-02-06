package nl.weeaboo.vn.core;

import nl.weeaboo.filesystem.FilePath;

public interface IScriptSeenLog {

    /**
     * Registers a script file.
     *
     * @param numTextLines The number of lines of text in the script file. If this script file was previously
     *        registered, but the number of lines has changed, the previously stored information is discarded.
     */
    void registerScriptFile(ResourceId resourceId, int numTextLines);

    /**
     * @see #hasSeenLine(ResourceId, int)
     */
    boolean hasSeenLine(FilePath filename, int lineNumber);

    /**
     * @param lineNumber A 1-based index, so valid line numbers are in the range {@code [1, numTextLines]}.
     * @return {@code true} if the specified line in the specified script file was previously marked as 'seen' by the
     *         user.
     */
    boolean hasSeenLine(ResourceId resourceId, int lineNumber);

    /**
     * @see #markLineSeen(ResourceId, int)
     */
    void markLineSeen(FilePath filename, int lineNumber);

    /**
     * Marks the specified line in the specified script file as 'seen' by the user.
     * @param lineNumber A 1-based index, so valid line numbers are in the range {@code [1, numTextLines]}.
     */
    void markLineSeen(ResourceId resourceId, int lineNumber);

}
