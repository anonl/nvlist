package nl.weeaboo.vn.impl.core;

import static nl.weeaboo.vn.impl.core.StorageTestHelper.KEY_BOOL;
import static nl.weeaboo.vn.impl.core.StorageTestHelper.KEY_DOUBLE;
import static nl.weeaboo.vn.impl.core.StorageTestHelper.KEY_INT;
import static nl.weeaboo.vn.impl.core.StorageTestHelper.KEY_STRING;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.impl.save.Storage;

public class StorageTest {

    private StorageTestHelper testHelper;
    private Storage storage;

    @Before
    public void before() {
        testHelper = new StorageTestHelper();
        storage = new Storage();
    }

    /** Check that default values are returned for missing attributes */
    @Test
    public void defaultValues() {
        String missing = "invalidKey";
        Assert.assertEquals(true, storage.getBoolean(missing, true));
        Assert.assertEquals(1, storage.getInt(missing, 1));
        Assert.assertEquals(.5, storage.getDouble(missing, .5), StorageTestHelper.EPSILON);
        Assert.assertEquals("test", storage.getString(missing, "test"));
    }

    @Test
    public void storeSimpleValues() {
        storage.setBoolean(KEY_BOOL, true);
        testHelper.assertBoolean(storage, KEY_BOOL, true);

        storage.setInt(KEY_INT, 1);
        testHelper.assertInt(storage, KEY_INT, 1);

        storage.setDouble(KEY_DOUBLE, .5);
        testHelper.assertDouble(storage, KEY_DOUBLE, .5);

        storage.setString(KEY_STRING, "test");
        testHelper.assertString(storage, KEY_STRING, "test");

        // Setting a value to null causes it to be removed
        storage.setString(KEY_STRING, null);
        Assert.assertFalse(storage.contains(KEY_STRING));
    }

    @Test
    public void addAll() {
        Storage testData = testHelper.createTestData();

        storage.addAll(testData);
        testHelper.assertStorageEquals(testData, storage);

        // Adding a storage to itself works
        storage.addAll("test", storage);
        Assert.assertEquals(testData.getKeys().size() * 2, storage.getKeys().size());
    }

    @Test
    public void clear() {
        Storage testData = testHelper.createTestData();

        testData.clear();
        Assert.assertEquals(0, testData.getKeys().size());
    }

    /** Test readObject/writeObject implementations (Java serialization) */
    @Test
    public void javaSerialize() {
        Storage testData = testHelper.createTestData();

        Storage deserialized = SerializeTester.reserialize(testData);

        testHelper.assertStorageEquals(testData, deserialized);
    }

}
