package nl.weeaboo.vn.save;

public interface ISaveFileHeader {

    long getCreationTime();

    public ThumbnailInfo getThumbnail();
    
    public IStorage getUserData();

}
