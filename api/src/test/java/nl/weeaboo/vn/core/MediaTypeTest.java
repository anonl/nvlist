package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.EnumTester;

public class MediaTypeTest {

    @Test
    public void checkEnumValuesChanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(562575701, EnumTester.hashEnum(MediaType.class));
    }

    @Test
    public void testSubFolders() {
        assertSubFolder(MediaType.IMAGE, "img/");
        assertSubFolder(MediaType.SOUND, "snd/");
        assertSubFolder(MediaType.VIDEO, "video/");
        assertSubFolder(MediaType.SCRIPT, "script/");
        assertSubFolder(MediaType.FONT, "font/");
        assertSubFolder(MediaType.OTHER, "");
    }

    private void assertSubFolder(MediaType mediaType, String expectedSubFolder) {
        Assert.assertEquals(FilePath.of(expectedSubFolder), mediaType.getSubFolder());
    }

}
