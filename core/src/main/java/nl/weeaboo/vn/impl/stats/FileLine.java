package nl.weeaboo.vn.impl.stats;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

/**
 * Represents a file path plus line number (usually obtained from a stack trace).
 */
public final class FileLine {

    private static final int INVALID_LINE_NUMBER = -1;

    private final FilePath filePath;
    private final int lineNumber;

    /**
     * @see #fromString(String)
     */
    public FileLine(FilePath filePath, int lineNumber) {
        this.filePath = Checks.checkNotNull(filePath);
        this.lineNumber = (lineNumber >= 1 ? lineNumber : INVALID_LINE_NUMBER);
    }

    /**
     * Extracts a {@link FileLine} object from a string representation of the form: {@code filename.ext:123}
     */
    public static FileLine fromString(String fileLineString) {
        // Initial assumption: string doesn't contain a line number
        String path = fileLineString;
        int line = -1;

        // Try to extract the line number
        int splitIndex = fileLineString.indexOf(':');
        if (splitIndex >= 0) {
            try {
                int number = Integer.parseInt(fileLineString.substring(splitIndex + 1));

                path = fileLineString.substring(0, splitIndex);
                line = number;
            } catch (NumberFormatException nfe) {
                // Second part of the string doesn't contain a valid line number
            }
        }

        return new FileLine(FilePath.of(path), line);
    }

    /**
     * Returns the file path.
     */
    public FilePath getFilePath() {
        return filePath;
    }

    /**
     * Returns the line number, or {@link #INVALID_LINE_NUMBER} if no line number is known.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int hashCode() {
        return filePath.hashCode() ^ lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileLine)) {
            return false;
        }

        FileLine other = (FileLine)obj;
        return filePath.equals(other.filePath) && lineNumber == other.lineNumber;
    }

    @Override
    public String toString() {
        if (lineNumber >= 1) {
            return filePath.toString() + ":" + lineNumber;
        } else {
            return filePath.toString();
        }
    }

}
