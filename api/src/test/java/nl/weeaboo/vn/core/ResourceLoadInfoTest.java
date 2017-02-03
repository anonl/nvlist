package nl.weeaboo.vn.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;

public class ResourceLoadInfoTest {

    private static final FilePath PATH = FilePath.of("path");
    private static final List<String> STACK_TRACE = Arrays.asList("a", "b", "c");

    @Test
    public void validArgs() {
        ResourceLoadInfo info = new ResourceLoadInfo(PATH, STACK_TRACE);
        Assert.assertEquals(PATH, info.getPath());
        Assert.assertEquals(STACK_TRACE, info.getCallStackTrace());

        info = new ResourceLoadInfo(PATH, Collections.<String>emptyList());
        Assert.assertEquals(Arrays.asList(), info.getCallStackTrace());
    }

    @Test
    public void invalidArgs() {
        assertInvalidArg(null, STACK_TRACE);
        assertInvalidArg(PATH, null);
    }

    private void assertInvalidArg(FilePath path, List<String> stackTrace) {
        try {
            ResourceLoadInfo info = new ResourceLoadInfo(path, stackTrace);
            Assert.fail("Expected exception, got object: " + info);
        } catch (IllegalArgumentException iae) {
            // This is expected
        }
    }

    @Test
    public void testAppenders() {
        ResourceLoadInfo info = new ResourceLoadInfo(FilePath.of("x"));
        assertPath("a", info.withPath(FilePath.of("a")));
        assertPath("xy", info.withFileSuffix("y"));
        assertPath("x#y", info.withSubId("y"));
        assertPath("x#y", info.withAppendedSubId("y"));
        assertPath("x#y-z", info.withSubId("y").withAppendedSubId("z"));
    }

    private void assertPath(String expected, ResourceLoadInfo loadInfo) {
        FilePath actualPath = loadInfo.getPath();
        Assert.assertEquals(expected, actualPath.toString());

        // ResourceLoadInfo.toString() prints the path
        Assert.assertEquals(actualPath.toString(), loadInfo.toString());
    }

}
