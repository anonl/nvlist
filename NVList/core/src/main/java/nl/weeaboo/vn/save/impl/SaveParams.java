package nl.weeaboo.vn.save.impl;

import nl.weeaboo.vn.core.impl.Storage;
import nl.weeaboo.vn.core.impl.UnmodifiableStorage;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.ThumbnailInfo;

public final class SaveParams {

    private final IStorage userData = new Storage();
    private ThumbnailInfo thumbnailInfo;
    private byte[] thumbnailData;

    public IStorage getUserData() {
        return UnmodifiableStorage.fromCopy(userData);
    }

    public void setUserData(IStorage data) {
        userData.clear();
        userData.addAll(data);
    }

    public ThumbnailInfo getThumbnailInfo() {
        return thumbnailInfo;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnail(ThumbnailInfo info, byte[] data) {
        thumbnailInfo = info;
        thumbnailData = data;
    }

}
