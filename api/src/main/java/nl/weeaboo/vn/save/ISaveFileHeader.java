package nl.weeaboo.vn.save;

public interface ISaveFileHeader {

    /**
     * Save file creation time in milliseconds since the Unix epoch.
     */
    long getCreationTime();

    /**
     * @return The optional thumbnail image for the save file, or {@code null} if no thumbnail exists.
     */
    ThumbnailInfo getThumbnail();

    /**
     * The user-data storage may be used to store additional application-specific data in a save file.
     */
    IStorage getUserData();

}
