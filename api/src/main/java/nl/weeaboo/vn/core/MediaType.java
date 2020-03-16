package nl.weeaboo.vn.core;

import nl.weeaboo.filesystem.FilePath;

/**
 * Resource type.
 */
public enum MediaType {
    IMAGE("img/"),
    SOUND("snd/"),
    VIDEO("video/"),
    SCRIPT("script/"),
    FONT("font/"),
    OTHER("");

    private final FilePath subFolder;

    private MediaType(String subFolder) {
        this.subFolder = FilePath.of(subFolder);
    }

    /**
     * Returns the sub-folder in which resources of this media type are stored.
     */
    public FilePath getSubFolder() {
        return subFolder;
    }
}
