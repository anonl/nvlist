package nl.weeaboo.vn.desktop.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.system.Platform;

/**
 * Test for {@link NameMapping}.
 */
public class NameMappingTest {

    private Path originalScriptFolder;

    @Before
    public void before() {
        originalScriptFolder = NameMapping.scriptFolder;
        NameMapping.scriptFolder = Paths.get("src/test/resources/script").toAbsolutePath();
    }

    @After
    public void after() {
        NameMapping.scriptFolder = originalScriptFolder;
    }

    @Test
    public void testRelativePath() {
        Assume.assumeTrue(Platform.get() == Platform.WINDOWS);

        Path baseFolder = NameMapping.scriptFolder;
        assertRelative(baseFolder.resolve("a.lvn"), "a.lvn");
    }

    private void assertRelative(Path absolutePath, String relativePath) {
        Assert.assertEquals(relativePath, NameMapping.toRelativeScriptPath(absolutePath.toString()));
    }

}