package nl.weeaboo.vn.save.impl;

import nl.weeaboo.vn.save.ISaveFileHeader;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.ThumbnailInfo;

final class SaveFileHeader implements ISaveFileHeader {

    private final long creationTime;
    private ThumbnailInfo thumbnail;
    private IStorage userData = new Storage();

    public SaveFileHeader(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public ThumbnailInfo getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailInfo thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public IStorage getUserData() {
        return UnmodifiableStorage.fromCopy(userData);
    }

    public void setUserData(IStorage data) {
        userData.clear();
        userData.addAll(data);
    }

}
