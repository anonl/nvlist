package nl.weeaboo.vn.save;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;

public final class ThumbnailInfo {

    private final String path;
    private final Dim imageSize;

    public ThumbnailInfo(String path, Dim imageSize) {
        this.path = Checks.checkNotNull(path);
        this.imageSize = Checks.checkNotNull(imageSize);
        Checks.checkRange(imageSize.w, "imageSize.w", 1);
        Checks.checkRange(imageSize.h, "imageSize.h", 1);
    }

    public String getPath() {
        return path;
    }

    public Dim getImageSize() {
        return imageSize;
    }

}