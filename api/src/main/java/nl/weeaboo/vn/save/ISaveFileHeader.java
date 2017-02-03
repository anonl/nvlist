package nl.weeaboo.vn.save;

public interface ISaveFileHeader {

    long getCreationTime();

    ThumbnailInfo getThumbnail();

    IStorage getUserData();

}
