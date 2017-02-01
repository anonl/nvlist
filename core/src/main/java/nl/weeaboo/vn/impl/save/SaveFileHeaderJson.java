package nl.weeaboo.vn.impl.save;

import java.io.IOException;

import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;

final class SaveFileHeaderJson {

    public int format = 1;
    public long creationTime;
    public Storage userData;
    public ThumbnailInfoJson thumbnail;
    
    public static SaveFileHeader decode(SaveFileHeaderJson headerJson) throws SaveFormatException, IOException {
        if (headerJson.format != SaveFileConstants.FORMAT_VERSION) {
            throw new SaveFormatException("Unsupported save format: " + headerJson.format);
        }

        // Return result
        SaveFileHeader header = new SaveFileHeader(headerJson.creationTime);
        header.setUserData(headerJson.userData);
        if (headerJson.thumbnail != null) {
            header.setThumbnail(ThumbnailInfoJson.decode(headerJson.thumbnail));
        }
        return header;
    }

    public static SaveFileHeaderJson encode(SaveFileHeader header) {        
        SaveFileHeaderJson headerJson = new SaveFileHeaderJson();        
        headerJson.format = SaveFileConstants.FORMAT_VERSION;
        headerJson.creationTime = header.getCreationTime();
        headerJson.userData = new Storage(header.getUserData());
        
        ThumbnailInfo thumbnail = header.getThumbnail();
        if (thumbnail != null) {
            headerJson.thumbnail = ThumbnailInfoJson.encode(thumbnail);
        }

        return headerJson;
    }

}
