package nl.weeaboo.vn.gdx.res;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.io.ZipUtil;
import nl.weeaboo.io.ZipUtil.Compression;
import nl.weeaboo.vn.gdx.HeadlessGdx;

public class DesktopGdxFileSystemTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private ExceptionTester exTester;
    private DesktopGdxFileSystem fileSystem;

    @BeforeClass
    public static void beforeAll() {
        HeadlessGdx.init();
    }

    @Before
    public void before() throws IOException {
        exTester = new ExceptionTester();

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
     * If a file archive exists, but can't be opened, it's treated as if it were empty.
     */
    @Test
    public void malformedFileArchive() throws IOException {
        // Replace res.nvl with garbage
        File arc1 = new File(tempFolder.getRoot(), "res.nvl");
        arc1.delete();
        Files.write("invalid", arc1, Charsets.UTF_8);

        assertFile("d", "res2.nvl"); // res2.nvl can be opened
        assertInvalidFile("folder/2"); // Files that should be in res.nvl can no longer be opened
        assertFile("c", "res2.nvl"); // Files that would normally resolve to res.nvl, no resolve to res2.nvl
    }

    private void assertChildren(String folderPath, String... expectedFilenames) {
        FileHandle folderHandle = fileSystem.resolve(folderPath);

        Set<String> actualNames = Arrays.asList(folderHandle.list())
                .stream()
                .map(FileHandle::name)
                .collect(Collectors.toSet());
        Assert.assertEquals(ImmutableSet.copyOf(expectedFilenames), actualNames);
    }

    private void assertFile(String filename, String expectedContents) {
        FilePath path = FilePath.of(filename);
        byte[] utf8 = StringUtil.toUTF8(expectedContents);

        // libGDX-compatible FileHandle subclass
        FileHandle handle = fileSystem.resolve(filename);

        Assert.assertEquals(true, fileSystem.getFileExists(path));
        Assert.assertEquals(true, handle.exists());

        try {
            Assert.assertEquals(utf8.length, fileSystem.getFileSize(path));
            Assert.assertEquals(utf8.length, handle.length());

            Assert.assertEquals(expectedContents, FileSystemUtil.readString(fileSystem, path));
            Assert.assertEquals(expectedContents, handle.readString());

            /*
             * The modified time depends on the timestamp of the external system running the test, but we
             * should at least get the same result from both fileSystem and handle.
             */
            Assert.assertEquals(handle.lastModified(), fileSystem.getFileModifiedTime(path));
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }
    }

    private void assertInvalidFile(String filename) {
        FilePath path = FilePath.of(filename);

        // libGDX-compatible FileHandle subclass
        FileHandle handle = fileSystem.resolve(filename);

        Assert.assertEquals(false, fileSystem.getFileExists(path));
        Assert.assertEquals(false, handle.exists());

        // Attempting to access file attributes or its contents should fail if the file doesn't exist
        exTester.expect(FileNotFoundException.class, () -> fileSystem.getFileSize(path));
        Assert.assertEquals(0L, handle.length());

        exTester.expect(FileNotFoundException.class, () -> fileSystem.getFileModifiedTime(path));
        Assert.assertEquals(0L, handle.lastModified());

        exTester.expect(FileNotFoundException.class, () -> fileSystem.openInputStream(path));
        // libGDX throws a runtime exception instead of an IOException
        exTester.expect(GdxRuntimeException.class, () -> Assert.assertEquals(0L, handle.readString()));
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

    private void writeFiles(String... paths) throws IOException {
        for (String path : paths) {
            // Write "file" to each of the specified files
            String parentPath = new File(path).getParent();
            if (!Strings.isNullOrEmpty(parentPath)) {
                tempFolder.newFolder(parentPath);
            }

            File file = tempFolder.newFile(path);
            Files.write("file", file, Charsets.UTF_8);
        }
    }

    private void assertIsDirectory(String path, boolean expectedResult) {
        FileHandle handle = fileSystem.resolve(path);
        Assert.assertEquals(expectedResult, handle.isDirectory());
    }

}
