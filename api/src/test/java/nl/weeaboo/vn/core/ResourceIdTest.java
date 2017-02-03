package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

import nl.weeaboo.filesystem.FilePath;

public class ResourceIdTest {

    @Test
    public void testEquals() {
        ResourceId scriptA1 = new ResourceId(MediaType.SCRIPT, FilePath.of("a"));
        ResourceId scriptA2 = new ResourceId(MediaType.SCRIPT, FilePath.of("a"), "");

        ResourceId imageA1 = new ResourceId(MediaType.IMAGE, FilePath.of("a"));
        ResourceId imageB1 = new ResourceId(MediaType.IMAGE, FilePath.of("b"));
        ResourceId imageB2 = new ResourceId(MediaType.IMAGE, FilePath.of("b"), "2");

        new EqualsTester()
            .addEqualityGroup(scriptA1, scriptA2)
            .addEqualityGroup(imageA1)
            .addEqualityGroup(imageB1)
            .addEqualityGroup(imageB2)
            .testEquals();
    }

    @Test
    public void testGetters() {
        ResourceId one = new ResourceId(MediaType.OTHER, FilePath.of("a/b"), "c");
        Assert.assertEquals(MediaType.OTHER, one.getType());
        Assert.assertEquals(FilePath.of("a/b"), one.getFilePath());
        Assert.assertEquals(true, one.hasSubId());
        Assert.assertEquals("c", one.getSubId());

        ResourceId two = new ResourceId(MediaType.SOUND, FilePath.of("d"));
        Assert.assertEquals(MediaType.SOUND, two.getType());
        Assert.assertEquals(FilePath.of("d"), two.getFilePath());
        Assert.assertEquals(false, two.hasSubId());
        Assert.assertEquals("", two.getSubId()); // No sub id -> empty string
    }

    @Test
    public void joinPathWithSubId() {
        assertJoinedPath("a/b", "c", "a/b#c");

        // Empty sub-id is omitted
        assertJoinedPath("a/b", "", "a/b");

        // Empty path is invalid
        assertJoinedPathInvalid("", "c");

        // Null is invalid
        assertJoinedPathInvalid("a/b", null);
        assertJoinedPathInvalid(null, "c");
    }

    private void assertJoinedPath(String path, String subId, String expectedOutput) {
        FilePath joined = ResourceId.toResourcePath(FilePath.of(path), subId);
        Assert.assertEquals(FilePath.of(expectedOutput), joined);

        // Check if the splitter functions can split the joined path back to its parts
        Assert.assertEquals(path, ResourceId.extractFilePath(joined.toString()).toString());
        Assert.assertEquals(subId, ResourceId.extractSubId(joined.toString()));
    }

    private void assertJoinedPathInvalid(String path, String subId) {
        try {
            ResourceId.toResourcePath(FilePath.of(path), subId);
            throw new AssertionError("Expected an exception: " + path + ", " + subId);
        } catch (NullPointerException | IllegalArgumentException iae) {
            // Expected
        }
    }

}
