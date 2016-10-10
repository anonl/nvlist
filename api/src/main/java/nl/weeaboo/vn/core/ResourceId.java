package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

public final class ResourceId implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final char SEPARATOR = '#';

    // --- Note: Update hashCode()/equals() ---
    private final MediaType type;
    private final FilePath filePath;
    private final String subResourceId;
    // --- Note: Update hashCode()/equals() ---

    public ResourceId(MediaType type, FilePath filePath) {
        this(type, filePath, "");
    }
    public ResourceId(MediaType type, FilePath filePath, String subResourceId) {
        this.type = Checks.checkNotNull(type);

        checkFilePath(filePath);
        this.filePath = filePath;

        checkSubId(subResourceId);
        this.subResourceId = subResourceId;
    }

    private static void checkFilePath(FilePath filePath) {
        String filePathString = filePath.toString();
        Checks.checkArgument(filePathString.length() > 0, "File path shouldn't be an empty string");
        checkSubId(filePathString);
    }

    private static void checkSubId(String subResourceId) {
        checkValidChars(subResourceId);
    }

    private static void checkValidChars(String str) {
        Checks.checkArgument(str.indexOf(SEPARATOR) < 0,
                "Illegal character '" + SEPARATOR + "' in name '" + str + "'");
    }

    @Override
    public int hashCode() {
        return type.hashCode() ^ filePath.hashCode() ^ subResourceId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ResourceId)) {
            return false;
        }

        ResourceId other = (ResourceId)obj;
        return type == other.type
            && filePath.equals(other.filePath)
            && subResourceId.equals(other.subResourceId);
    }

    @Override
    public String toString() {
        if (subResourceId.length() > 0) {
            return filePath.toString() + SEPARATOR + subResourceId;
        } else {
            return filePath.toString();
        }
    }

    public MediaType getType() {
        return type;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public boolean hasSubId() {
        return subResourceId.length() > 0;
    }

    public String getSubId() {
        return subResourceId;
    }

    /**
     * Combines a file path and a sub-resource id into a single string.
     *
     * @see #getFilePath(String)
     * @see #getSubId(String)
     */
    public static FilePath toResourcePath(FilePath filePath, String subId) {
        checkFilePath(filePath);
        checkSubId(subId);

        if (subId.length() == 0) {
            return filePath;
        }
        return FilePath.of(filePath.toString() + SEPARATOR + subId);
    }

    /**
     * Extracts the file part from a resource id string.
     * <p>
     * Example: {@code "a/b.png#sub" -> "a/b.png"}
     */
    public static FilePath getFilePath(String path) {
        int index = path.indexOf(SEPARATOR);
        return FilePath.of(index < 0 ? path : path.substring(0, index));
    }

    /**
     * Extracts the sub id part from a resource id string.
     * <p>
     * Example: {@code "a/b.png#sub" -> "sub"}
     */
    public static String getSubId(String path) {
        int index = path.indexOf(SEPARATOR);
        return (index < 0 ? "" : path.substring(index + 1));
    }
}
