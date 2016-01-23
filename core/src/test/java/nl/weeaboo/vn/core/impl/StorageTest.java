package nl.weeaboo.vn.core.impl;

import static nl.weeaboo.vn.CoreTestUtil.deserializeObject;
import static nl.weeaboo.vn.CoreTestUtil.serializeObject;
import static nl.weeaboo.vn.core.impl.StorageTestHelper.KEY_BOOL;
import static nl.weeaboo.vn.core.impl.StorageTestHelper.KEY_DOUBLE;
import static nl.weeaboo.vn.core.impl.StorageTestHelper.KEY_INT;
import static nl.weeaboo.vn.core.impl.StorageTestHelper.KEY_STRING;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void javaSerialize() throws IOException, ClassNotFoundException {
        Storage testData = testHelper.createTestData();

        Storage deserialized = deserializeObject(serializeObject(testData), Storage.class);

        testHelper.assertStorageEquals(testData, deserialized);
    }

}
