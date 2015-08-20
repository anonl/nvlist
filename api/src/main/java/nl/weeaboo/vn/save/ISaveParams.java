package nl.weeaboo.vn.save;

public interface ISaveParams {

    IStorage getUserData();

    ThumbnailInfo getThumbnailInfo();

    byte[] getThumbnailData();

}
