package nl.weeaboo.vn.save;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Dim;

public class ThumbnailInfoTest {

    @Test
    public void validArgs() {
        ThumbnailInfo info = new ThumbnailInfo("path", new Dim(23, 45));
        Assert.assertEquals("path", info.getPath());
        Assert.assertEquals(new Dim(23, 45), info.getImageSize());
    }

    @Test
    public void invalidArgs() {
        assertInvalidArg(null, new Dim(23, 45));
        assertInvalidArg("path", null);
        assertInvalidArg("path", new Dim(0, 45));
        assertInvalidArg("path", new Dim(23, 0));
    }

    private void assertInvalidArg(String path, Dim imageSize) {
        try {
            ThumbnailInfo info = new ThumbnailInfo(path, imageSize);
            Assert.fail("Expected exception, got object: " + info);
        } catch (IllegalArgumentException iae) {
            // This is expected
        }
    }

}
