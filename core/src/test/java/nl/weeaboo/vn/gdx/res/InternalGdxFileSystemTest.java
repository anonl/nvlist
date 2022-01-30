package nl.weeaboo.vn.gdx.res;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import nl.weeaboo.filesystem.FilePath;

public class InternalGdxFileSystemTest extends AbstractGdxFileSystemTest {

    private InternalGdxFileSystem fileSystem;

    @Before
    public void before() throws IOException {
        writeFiles("a", "b", "folder/1");

        String rootPath = tempFolder.getRoot().getPath();
        fileSystem = new InternalGdxFileSystem(rootPath + "/");
    }

    @After
    public void after() {
        fileSystem.close();
    }

    @Override
    protected GdxFileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * File paths are always case-sensitive, regardless of the underlying file system. This is to prevent
     * accidental platform-specific behavior when developing on Windows (which has a case-insensitive file
     * system by default).
     */
    @Test
    public void testCaseSensitiveNames() {
        assertFile("a", "file");
        assertInvalidFile("A"); // Same name, different case -> invalid

        assertIsDirectory("folder", true);
        assertIsDirectory("Folder", false); // Same name, different case -> invalid
    }

    @Test
    public void testInvalidFileHandle() {
        FileHandle handle = fileSystem.resolve(FilePath.of("invalid"));

        Assert.assertThrows(GdxRuntimeException.class, () -> handle.read());
        Assert.assertEquals(true, handle.parent().exists()); // Root folder does exist
        Assert.assertEquals(false, handle.child("also-invalid").exists());
        Assert.assertEquals(false, handle.isDirectory());
        Assert.assertArrayEquals(new FileHandle[0], handle.list());
        Assert.assertEquals(0L, handle.length());
        Assert.assertEquals(false, handle.exists());
    }

}
