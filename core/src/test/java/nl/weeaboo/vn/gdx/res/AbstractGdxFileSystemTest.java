package nl.weeaboo.vn.gdx.res;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.io.FileUtil;
import nl.weeaboo.vn.gdx.HeadlessGdx;

public abstract class AbstractGdxFileSystemTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeAll() {
        HeadlessGdx.init();
    }

    protected abstract GdxFileSystem getFileSystem();

    protected void assertChildren(String folderPath, String... expectedFilenames) {
        FileHandle folderHandle = getFileSystem().resolve(folderPath);

        Set<String> actualNames = Arrays.asList(folderHandle.list())
                .stream()
                .map(FileHandle::name)
                .collect(Collectors.toSet());
        Assert.assertEquals(ImmutableSet.copyOf(expectedFilenames), actualNames);
    }

    protected void assertFile(String filename, String expectedContents) {
        FilePath path = FilePath.of(filename);
        byte[] utf8 = StringUtil.toUTF8(expectedContents);

        // libGDX-compatible FileHandle subclass
        FileHandle handle = getFileSystem().resolve(filename);

        Assert.assertEquals(true, getFileSystem().getFileExists(path));
        Assert.assertEquals(true, handle.exists());

        try {
            Assert.assertEquals(utf8.length, getFileSystem().getFileSize(path));
            Assert.assertEquals(utf8.length, handle.length());

            Assert.assertEquals(expectedContents, FileSystemUtil.readString(getFileSystem(), path));
            Assert.assertEquals(expectedContents, handle.readString());

            /*
             * The modified time depends on the timestamp of the external system running the test, but we
             * should at least get the same result from both getFileSystem() and handle.
             */
            Assert.assertEquals(handle.lastModified(), getFileSystem().getFileModifiedTime(path));
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }
    }

    protected void assertInvalidFile(String filename) {
        FilePath path = FilePath.of(filename);

        // libGDX-compatible FileHandle subclass
        FileHandle handle = getFileSystem().resolve(filename);

        Assert.assertEquals(false, getFileSystem().getFileExists(path));
        Assert.assertEquals(false, handle.exists());

        // Attempting to access file attributes or its contents should fail if the file doesn't exist
        Assert.assertThrows(FileNotFoundException.class, () -> getFileSystem().getFileSize(path));
        Assert.assertEquals(0L, handle.length());

        Assert.assertThrows(FileNotFoundException.class, () -> getFileSystem().getFileModifiedTime(path));
        Assert.assertEquals(0L, handle.lastModified());

        Assert.assertThrows(FileNotFoundException.class, () -> getFileSystem().openInputStream(path));
        // libGDX throws a runtime exception instead of an IOException
        Assert.assertThrows(GdxRuntimeException.class, () -> handle.readString());
    }

    protected void writeFiles(String... paths) throws IOException {
        for (String path : paths) {
            // Write "file" to each of the specified files
            String parentPath = new File(path).getParent();
            if (!Strings.isNullOrEmpty(parentPath)) {
                tempFolder.newFolder(parentPath);
            }

            File file = tempFolder.newFile(path);
            FileUtil.writeUtf8(file, "file");
        }
    }

    protected void assertIsDirectory(String path, boolean expectedResult) {
        FileHandle handle = getFileSystem().resolve(path);
        Assert.assertEquals(expectedResult, handle.isDirectory());
    }

}
