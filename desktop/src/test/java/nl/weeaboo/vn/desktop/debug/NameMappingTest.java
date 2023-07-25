package nl.weeaboo.vn.desktop.debug;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.lwjgl.system.Platform;

/**
 * Test for {@link NameMapping}.
 */
public class NameMappingTest extends DebugAdapterTest {

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