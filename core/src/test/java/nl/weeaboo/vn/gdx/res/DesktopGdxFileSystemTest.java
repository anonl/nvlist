package nl.weeaboo.vn.gdx.res;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.io.FileUtil;
import nl.weeaboo.io.ZipUtil;
import nl.weeaboo.io.ZipUtil.Compression;

public final class DesktopGdxFileSystemTest extends AbstractGdxFileSystemTest {

    private DesktopGdxFileSystem fileSystem;

    @Before
    public void before() throws IOException {
        /*
         * Generate filesystem contents
         *
         * res/a.txt
         * res/c.txt (overridden by res2.nvl)
         *
         * res.nvl/b.txt
         * res.nvl/c.txt (overridden by res2.nvl)
         *
         * res2.nvl/c.txt
         */
        writeFiles("a", "b", "folder/1");
        writeZipFile("res.nvl", "b", "c", "folder/1", "folder/2");
        writeZipFile("res2.nvl", "c", "d");

        String rootPath = tempFolder.getRoot().getPath();
        fileSystem = new DesktopGdxFileSystem(rootPath + "/");
    }

    @After
    public void after() {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    @Override
    protected GdxFileSystem getFileSystem() {
        return fileSystem;
    }

    /** Test basic file operations (read, length, exists) */
    @Test
    public void basicFileSystemOperations() {
        assertFile("a", "file");
        assertFile("b", "file"); // Regular files override archive files
        assertFile("c", "res.nvl");
        assertFile("d", "res2.nvl"); // Higher numbered archives override lower numbers
        assertInvalidFile("e");
    }

    @Test
    public void directoryBrowsing() {
        // List files in the folder (merges results from all archives)
        assertChildren("folder", "1", "2");

        // List files in the root folder
        assertChildren("", "a", "b", "c", "d", "folder", "res.nvl", "res2.nvl");

        // Attempt to list children for non-directory paths and invalid paths
        assertChildren("a");
        assertChildren("res.nvl");
        assertChildren("invalid/");
    }

    /**
     * Test for {@link FileHandle#isDirectory()}
     */
    @Test
    public void testIsDirectory() {
        assertIsDirectory("", true); // Root folder
        assertIsDirectory("/", true); // Root folder

        assertIsDirectory("a", false);
        assertIsDirectory("res.nvl", false);

        assertIsDirectory("folder", true);
        assertIsDirectory("folder/", true);
    }

    /**
     * Walk through the directory structure using {@link FileHandle#parent()}/{@link FileHandle#child(String)}
     */
    @Test
    public void directoryTreeWalk() {
        FileHandle root = fileSystem.resolve("");
        for (FileHandle file : root.list()) {
            // list() and child() return the same results
            Assert.assertEquals(root.child(file.name()), file);

            // handle.child().parent() is equal to handle
            Assert.assertEquals(root, file.parent());
        }
    }

    /**
     * List directory contents using various overloads of {@code FileHandle.list()}.
     */
    @Test
    public void testDirectoryListing() {
        FileHandle root = fileSystem.resolve("");

        // list(FilenameFilter)
        for (FileHandle file : root.list((dir, name) -> name.equals("a"))) {
            Assert.assertEquals("a", file.name());
        }

        // list(FileFilter)
        for (FileHandle file : root.list(file -> file.getName().equals("a"))) {
            Assert.assertEquals("a", file.name());
        }

        // list(String)
        for (FileHandle file : root.list("a")) {
            Assert.assertEquals("a", file.name());
        }
    }

    /**
     * Get the sibling of a file in a directory.
     */
    @Test
    public void testSibling() {
        FileHandle a = fileSystem.resolve("a");

        Assert.assertEquals(a.sibling("b"), fileSystem.resolve("b"));
    }

    /**
     * If a file archive exists, but can't be opened, it's treated as if it were empty.
     */
    @Test
    public void malformedFileArchive() throws IOException {
        // Replace res.nvl with garbage
        File arc1 = new File(tempFolder.getRoot(), "res.nvl");
        arc1.delete();
        FileUtil.writeUtf8(arc1, "invalid");

        assertFile("d", "res2.nvl"); // res2.nvl can be opened
        assertInvalidFile("folder/2"); // Files that should be in res.nvl can no longer be opened
        assertFile("c", "res2.nvl"); // Files that would normally resolve to res.nvl, no resolve to res2.nvl
    }

    /**
     * Files in a file archive can't be deleted.
     */
    @Test
    public void testDelete() {
        FileHandle file = fileSystem.resolve("c");

        Assert.assertThrows(GdxRuntimeException.class, () -> file.delete());
        Assert.assertThrows(GdxRuntimeException.class, () -> file.deleteDirectory());
        Assert.assertThrows(GdxRuntimeException.class, () -> file.emptyDirectory());
    }

    /**
     * Files in a file archive can't be written to.
     */
    @Test
    public void testWrite() {
        FileHandle file = fileSystem.resolve("c");

        Assert.assertThrows(GdxRuntimeException.class, () -> file.write(false));
        Assert.assertThrows(GdxRuntimeException.class, () -> file.writer(false, "ASCII"));
    }


    private void writeZipFile(String zipFileName, String... files) throws IOException {
        File zipFile = tempFolder.newFile(zipFileName);
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String filename : files) {
                byte[] data = StringUtil.toUTF8(zipFileName);
                ZipUtil.writeFileEntry(zout, filename, data, 0, data.length, Compression.NONE);
            }
        }
    }

}
