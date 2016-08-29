package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;

public final class ResourceId implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final char SEPARATOR = '#';

    private final MediaType type;
    private final String filePath;
    private final String subResourceId;

    public ResourceId(MediaType type, String filePath) {
        this(type, filePath, "");
    }
    public ResourceId(MediaType type, String filePath, String subResourceId) {
        this.type = Checks.checkNotNull(type);
        this.filePath = checkValidChars(filePath);
        this.subResourceId = checkValidChars(subResourceId);
    }

    private static String checkValidChars(String path) {
        Checks.checkNotNull(path);
        Checks.checkArgument(path.indexOf(SEPARATOR) < 0,
                "Illegal character '" + SEPARATOR + "' in name '" + path + "'");
        return path;
    }

    @Override
    public String toString() {
        return filePath + SEPARATOR + subResourceId;
    }

    public MediaType getType() {
        return type;
    }

    public String getFilePath() {
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
    public static String toResourcePath(String filePath, String subId) {
        checkValidChars(filePath);
        checkValidChars(subId);

        if (subId.length() == 0) {
            return filePath;
        }
        return filePath + SEPARATOR + subId;
    }

    /**
     * Extracts the file part from a resource id string.
     * <p>
     * Example: {@code "a/b.png#sub" -> "a/b.png"}
     */
    public static String getFilePath(String path) {
        int index = path.indexOf(SEPARATOR);
        return (index < 0 ? path : path.substring(0, index));
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
