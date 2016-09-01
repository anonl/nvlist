package nl.weeaboo.vn.save;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public final class ThumbnailInfo {

    private final FilePath path;
    private final Dim imageSize;

    public ThumbnailInfo(FilePath path, Dim imageSize) {
        this.path = Checks.checkNotNull(path);
        this.imageSize = Checks.checkNotNull(imageSize);
        Checks.checkRange(imageSize.w, "imageSize.w", 1);
        Checks.checkRange(imageSize.h, "imageSize.h", 1);
    }

    public FilePath getPath() {
        return path;
    }

    public Dim getImageSize() {
        return imageSize;
    }

}