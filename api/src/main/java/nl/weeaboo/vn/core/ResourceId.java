package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

public final class ResourceId implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final char SEPARATOR = '#';

    private final MediaType type;
    private final FilePath filePath;
    private final String subResourceId;

    public ResourceId(MediaType type, FilePath filePath) {
        this(type, filePath, "");
    }
    public ResourceId(MediaType type, FilePath filePath, String subResourceId) {
        this.type = Checks.checkNotNull(type);

        checkValidChars(filePath.toString());
        this.filePath = filePath;

        checkValidChars(subResourceId);
        this.subResourceId = subResourceId;
    }

    private static void checkValidChars(String str) {
        Checks.checkArgument(str.indexOf(SEPARATOR) < 0,
                "Illegal character '" + SEPARATOR + "' in name '" + str + "'");
    }

    @Override
    public String toString() {
        return filePath.toString() + SEPARATOR + subResourceId;
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
        checkValidChars(filePath.toString());
        checkValidChars(subId);

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
