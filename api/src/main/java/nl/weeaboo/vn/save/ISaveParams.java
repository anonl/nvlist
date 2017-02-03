package nl.weeaboo.vn.save;

/**
 * Contains information required to create a new save file.
 */
public interface ISaveParams {

    /**
     * The user-data storage may be used to store additional application-specific data in a save file.
     *
     * @see ISaveFileHeader#getUserData()
     */
    IStorage getUserData();

    /**
     * @return Metadata concerning the optional thumbnail image, or {@code null} if no thumbnail is set.
     * @see ISaveFileHeader#getThumbnail()
     */
    ThumbnailInfo getThumbnailInfo();

    /**
     * @return The thumbnail image data, or {@code null} if no thumbnail is set.
     */
    byte[] getThumbnailData();

}
