package nl.weeaboo.vn.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ResourceLoadInfoTest {

    private static final List<String> STACK_TRACE = Arrays.asList("a", "b", "c");

    @Test
    public void validArgs() {
        ResourceLoadInfo info = new ResourceLoadInfo("path", STACK_TRACE);
        Assert.assertEquals("path", info.getFilename());
        Assert.assertEquals(STACK_TRACE, info.getCallStackTrace());

        info = new ResourceLoadInfo("path", Collections.<String> emptyList());
        Assert.assertEquals(Arrays.asList(), info.getCallStackTrace());
    }

    @Test
    public void invalidArgs() {
        assertInvalidArg(null, STACK_TRACE);
        assertInvalidArg("path", null);
    }

    private void assertInvalidArg(String path, List<String> stackTrace) {
        try {
            ResourceLoadInfo info = new ResourceLoadInfo(path, stackTrace);
            Assert.fail("Expected exception, got object: " + info);
        } catch (IllegalArgumentException iae) {
            // This is expected
        }
    }

}
