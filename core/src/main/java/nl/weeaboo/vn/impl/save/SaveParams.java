package nl.weeaboo.vn.impl.save;

import nl.weeaboo.vn.save.ISaveParams;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.ThumbnailInfo;

public final class SaveParams implements ISaveParams {

    private final IStorage userData = new Storage();
    private ThumbnailInfo thumbnailInfo;
    private byte[] thumbnailData;

    @Override
    public IStorage getUserData() {
        return UnmodifiableStorage.fromCopy(userData);
    }

    /**
     * @see #getUserData()
     */
    public void setUserData(IStorage data) {
        userData.clear();
        userData.addAll(data);
    }

    @Override
    public ThumbnailInfo getThumbnailInfo() {
        return thumbnailInfo;
    }

    @Override
    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    /**
     * @see #getThumbnailData()
     */
    public void setThumbnail(ThumbnailInfo info, byte[] data) {
        thumbnailInfo = info;
        thumbnailData = data;
    }

}
