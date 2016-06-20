package nl.weeaboo.vn.save.impl;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Dim;
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
        header.setThumbnail(new ThumbnailInfo("abc", Dim.of(12, 34)));

        SaveFileHeaderJson json = SaveFileHeaderJson.encode(header);

        String str = JsonUtil.toJson(json);
        SaveFileHeaderJson deserialized = JsonUtil.fromJson(SaveFileHeaderJson.class, str);

        SaveFileHeader decoded = SaveFileHeaderJson.decode(deserialized);
        Assert.assertEquals(str, JsonUtil.toJson(SaveFileHeaderJson.encode(decoded)));
    }

}
