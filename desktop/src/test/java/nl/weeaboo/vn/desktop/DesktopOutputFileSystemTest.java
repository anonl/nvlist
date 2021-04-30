package nl.weeaboo.vn.desktop;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.vn.gdx.HeadlessGdx;

public final class DesktopOutputFileSystemTest {

    private static final FileType TYPE = FileType.External;
    private static final String PREFIX = "prefix";

    private DesktopOutputFileSystem fs;

    @Before
    public void before() {
        HeadlessGdx.init();
        fs = new DesktopOutputFileSystem(TYPE, PREFIX);
    }

    @Test
    public void testResolve() {
        FileHandle handle = fs.resolve("name");
        Assert.assertEquals(PREFIX + "name", handle.name());
    }

}
