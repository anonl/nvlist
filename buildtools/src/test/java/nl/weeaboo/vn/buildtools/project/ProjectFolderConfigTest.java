package nl.weeaboo.vn.buildtools.project;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public final class ProjectFolderConfigTest {

    /** Test the default folders of the no-arg constructor */
    @Test
    public void testDefaultValues() {
        ProjectFolderConfig pfc = new ProjectFolderConfig();
        Assert.assertEquals(Paths.get(""), pfc.getProjectFolder());
        Assert.assertEquals(Paths.get("res"), pfc.getResFolder());
        Assert.assertEquals(Paths.get("build-res"), pfc.getBuildResFolder());
        Assert.assertEquals(Paths.get("build-out"), pfc.getBuildOutFolder());
        Assert.assertEquals(Paths.get("build-res/build.properties"), pfc.getBuildPropertiesFile());
        Assert.assertEquals(Paths.get("build-tools"), pfc.getBuildToolsFolder());
    }

    /** Test for {@link ProjectFolderConfig#withProjectFolder(Path)} */
    @Test
    public void testWithProjectFolder() {
        ProjectFolderConfig pfc = new ProjectFolderConfig();
        Assert.assertEquals(Paths.get(""), pfc.getProjectFolder());

        pfc = pfc.withProjectFolder(Paths.get("other"));
        Assert.assertEquals(Paths.get("other"), pfc.getProjectFolder());
    }

    /** Test the string representation */
    @Test
    public void testToString() {
        ProjectFolderConfig pfc = new ProjectFolderConfig();
        Assert.assertEquals("ProjectFolderConfig[projectFolder=, buildToolsFolder=build-tools]",
                pfc.toString());
    }

    @Test
    public void testToCanonicalPath() {
        String workDir = Paths.get("").toAbsolutePath().toString();
        Assert.assertEquals(workDir, ProjectFolderConfig.toCanonicalPath(Paths.get(".//")));
    }

}
