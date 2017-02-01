package nl.weeaboo.vn.impl.core;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.impl.save.Storage;
import nl.weeaboo.vn.impl.save.StorageIO;
import nl.weeaboo.vn.impl.test.TestFileSystem;
import nl.weeaboo.vn.save.IStorage;

public class StorageIOTest {

    private static final FilePath FILENAME = FilePath.of("test.json");

    private StorageTestHelper testHelper;
    private MultiFileSystem fileSystem;

    private Storage testData;

    @Before
    public void before() {
        testHelper = new StorageTestHelper();
        fileSystem = TestFileSystem.newInstance();

        testData = testHelper.createTestData();
    }

    @Test
    public void basicReadWrite() throws IOException {
        IWritableFileSystem fs = fileSystem.getWritableFileSystem();
        StorageIO.write(testData, fs, FILENAME);
        IStorage deserialized = StorageIO.read(fs, FILENAME);

        testHelper.assertStorageEquals(testData, deserialized);
    }

    @Test
    public void secureWriterReadWrite() throws IOException {
        SecureFileWriter sfw = new SecureFileWriter(fileSystem.getWritableFileSystem());

        StorageIO.write(testData, sfw, FILENAME);
        IStorage deserialized = StorageIO.read(sfw, FILENAME);

        testHelper.assertStorageEquals(testData, deserialized);

    }

}
