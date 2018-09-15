package nl.weeaboo.vn.buildtools.file;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;

public final class FilePathPatternTest {

    @Test
    public void testGlob() {
        FilePathPattern anyJpg = FilePathPattern.fromGlob("**/*.jpg");
        assertMatch(anyJpg, "img/a.jpg");
        assertNotMatch(anyJpg, "a.jpg");

        FilePathPattern anyImage = FilePathPattern.fromGlob("img/*.*");
        assertMatch(anyImage, "img/a.png");
        assertNotMatch(anyImage, "snd/img/a.png");

        // TODO: Should img/* match img/a/b?
    }

    private void assertMatch(FilePathPattern pattern, String path) {
        Assert.assertTrue(pattern.matches(FilePath.of(path)));
    }

    private void assertNotMatch(FilePathPattern pattern, String path) {
        Assert.assertFalse(pattern.matches(FilePath.of(path)));
    }

}
