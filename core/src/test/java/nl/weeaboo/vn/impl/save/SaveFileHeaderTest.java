package nl.weeaboo.vn.impl.save;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.save.JsonUtil;
import nl.weeaboo.vn.impl.save.SaveFileHeader;
import nl.weeaboo.vn.impl.save.SaveFileHeaderJson;
import nl.weeaboo.vn.impl.save.Storage;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;

public class SaveFileHeaderTest {

    @Test
    public void serialize() throws SaveFormatException, IOException {
        Storage userData = new Storage();
        userData.setBoolean("bool", true);
        userData.setInt("int", 123);
        userData.setDouble("double", 1.23);
        userData.setString("string", "str\nval");
        userData.setString("nullString", null); // Null values are not stored

        SaveFileHeader header = new SaveFileHeader(12345);
        header.setUserData(userData);
        header.setThumbnail(new ThumbnailInfo(FilePath.of("abc"), Dim.of(12, 34)));

        SaveFileHeaderJson json = SaveFileHeaderJson.encode(header);

        String str = JsonUtil.toJson(json);
        SaveFileHeaderJson deserialized = JsonUtil.fromJson(SaveFileHeaderJson.class, str);

        SaveFileHeader decoded = SaveFileHeaderJson.decode(deserialized);
        Assert.assertEquals(str, JsonUtil.toJson(SaveFileHeaderJson.encode(decoded)));
    }

}
