package nl.weeaboo.vn.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;

public class ResourceLoadInfoTest {

    private static final MediaType MEDIA_TYPE = MediaType.IMAGE;
    private static final FilePath PATH = FilePath.of("path");
    private static final List<String> STACK_TRACE = Arrays.asList("a", "b", "c");

    @Test
    public void validArgs() {
        ResourceLoadInfo info = new ResourceLoadInfo(MEDIA_TYPE, PATH, STACK_TRACE);
        Assert.assertEquals(MEDIA_TYPE, info.getMediaType());
        Assert.assertEquals(PATH, info.getPath());
        Assert.assertEquals(STACK_TRACE, info.getCallStackTrace());

        info = new ResourceLoadInfo(MEDIA_TYPE, PATH, Collections.<String>emptyList());
        Assert.assertEquals(Arrays.asList(), info.getCallStackTrace());
    }

    @Test
    public void invalidArgs() {
        assertInvalidArg(null, STACK_TRACE);
        assertInvalidArg(PATH, null);
    }

    private void assertInvalidArg(FilePath path, List<String> stackTrace) {
        try {
            ResourceLoadInfo info = new ResourceLoadInfo(MEDIA_TYPE, path, stackTrace);
            Assert.fail("Expected exception, got object: " + info);
        } catch (IllegalArgumentException iae) {
            // This is expected
        }
    }

    @Test
    public void testAppenders() {
        final ResourceLoadInfo x = loadInfo("x");
        final ResourceLoadInfo xDash = loadInfo("x-");
        final ResourceLoadInfo xSubYDash = loadInfo("x#y-");
        final ResourceLoadInfo xWithExt = loadInfo("x.ext");

        assertPath("a", x.withPath(FilePath.of("a")));
        assertPath("xy", x.withFileSuffix("y"));
        assertPath("xy.ext", xWithExt.withFileSuffix("y"));
        assertPath("x#y", x.withSubId("y"));
        assertPath("x#y", x.withAppendedSubId("y"));
        assertPath("x-#y", xDash.withAppendedSubId("y"));
        assertPath("x#y-z", xSubYDash.withAppendedSubId("z"));
        assertPath("x#y-z", x.withSubId("y").withAppendedSubId("z"));
    }

    private ResourceLoadInfo loadInfo(String path) {
        return new ResourceLoadInfo(MEDIA_TYPE, FilePath.of(path));
    }

    private void assertPath(String expected, ResourceLoadInfo loadInfo) {
        FilePath actualPath = loadInfo.getPath();
        Assert.assertEquals(expected, actualPath.toString());

        // ResourceLoadInfo.toString() prints the path
        Assert.assertEquals(actualPath.toString(), loadInfo.toString());
    }

}
