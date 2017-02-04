package nl.weeaboo.vn.save;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public final class ThumbnailInfo {

    public static final FilePath DEFAULT_THUMBNAIL_PATH = FilePath.of("thumbnail.jpg");

    private final FilePath path;
    private final Dim imageSize;

    public ThumbnailInfo(Dim imageSize) {
        this(DEFAULT_THUMBNAIL_PATH, imageSize);
    }

    public ThumbnailInfo(FilePath path, Dim imageSize) {
        this.path = Checks.checkNotNull(path);

        this.imageSize = Checks.checkNotNull(imageSize);
        Checks.checkRange(imageSize.w, "imageSize.w", 1);
        Checks.checkRange(imageSize.h, "imageSize.h", 1);
    }

    /**
     * @return The relative path of the thumbnail image within the save file.
     */
    public FilePath getPath() {
        return path;
    }

    /**
     * The pixel dimensions of the thumbnail image.
     */
    public Dim getImageSize() {
        return imageSize;
    }

}