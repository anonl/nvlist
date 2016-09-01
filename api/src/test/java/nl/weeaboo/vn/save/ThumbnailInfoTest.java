package nl.weeaboo.vn.save;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public class ThumbnailInfoTest {

    private static final FilePath PATH = FilePath.of("path");

    @Test
    public void validArgs() {
        ThumbnailInfo info = new ThumbnailInfo(PATH, Dim.of(23, 45));
        Assert.assertEquals(FilePath.of("path"), info.getPath());
        Assert.assertEquals(Dim.of(23, 45), info.getImageSize());
    }

    @Test
    public void invalidArgs() {
        assertInvalidArg(null, Dim.of(23, 45));
        assertInvalidArg(PATH, null);
        assertInvalidArg(PATH, Dim.of(0, 45));
        assertInvalidArg(PATH, Dim.of(23, 0));
    }

    private void assertInvalidArg(FilePath path, Dim imageSize) {
        try {
            ThumbnailInfo info = new ThumbnailInfo(path, imageSize);
            Assert.fail("Expected exception, got object: " + info);
        } catch (IllegalArgumentException iae) {
            // This is expected
        }
    }

}
