package nl.weeaboo.vn.core.impl;

import static nl.weeaboo.vn.CoreTestUtil.deserializeObject;
import static nl.weeaboo.vn.CoreTestUtil.serializeObject;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.CoreTestUtil;

public class StorageTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private static final String KEY_BOOL = "keyBool";
    private static final String KEY_INT = "keyInt";
    private static final String KEY_DOUBLE = "keyDouble";
    private static final String KEY_STRING = "keyString";

    private Storage storage;

    @Before
    public void before() {
        storage = new Storage();
    }

    /** Check that default values are returned for missing attributes */
    @Test
    public void defaultValues() {
        String missing = "invalidKey";
        Assert.assertEquals(true, storage.getBoolean(missing, true));
        Assert.assertEquals(1, storage.getInt(missing, 1));
        Assert.assertEquals(.5, storage.getDouble(missing, .5), EPSILON);
        Assert.assertEquals("test", storage.getString(missing, "test"));
    }

    @Test
    public void storeSimpleValues() {
        storage.setBoolean(KEY_BOOL, true);
        assertBoolean(KEY_BOOL, true);

        storage.setInt(KEY_INT, 1);
        assertInt(KEY_INT, 1);

        storage.setDouble(KEY_DOUBLE, .5);
        assertDouble(KEY_DOUBLE, .5);

        storage.setString(KEY_STRING, "test");
        assertString(KEY_STRING, "test");

        // Setting a value to null causes it to be removed
        storage.setString(KEY_STRING, null);
        Assert.assertFalse(storage.contains(KEY_STRING));
    }

    @Test
    public void addAll() {
        Storage testData = createTestData();

        storage.addAll(testData);
        assertStorageEquals(testData, storage);

        // Adding a storage to itself works
        storage.addAll("test", storage);
        Assert.assertEquals(testData.getKeys().size() * 2, storage.getKeys().size());
    }

    @Test
    public void clear() {
        Storage testData = createTestData();

        testData.clear();
        Assert.assertEquals(0, testData.getKeys().size());
    }

    /** Test readObject/writeObject implementations (Java serialization) */
    @Test
    public void javaSerialize() throws IOException, ClassNotFoundException {
        Storage testData = createTestData();

        Storage deserialized = deserializeObject(serializeObject(testData), Storage.class);

        assertStorageEquals(testData, deserialized);
    }

    private static void assertStorageEquals(Storage expected, Storage actual) {
        Assert.assertEquals(Storage.toJson(expected), Storage.toJson(actual));
    }

    private void assertBoolean(String key, boolean expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getBoolean(key, false));
    }
    private void assertInt(String key, int expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getInt(key, 0));
    }
    private void assertDouble(String key, double expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getDouble(key, 0), EPSILON);
    }
    private void assertString(String key, String expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getString(key, null));
    }

    private static Storage createTestData() {
        Storage storage = new Storage();
        storage.setBoolean(KEY_BOOL, true);
        storage.setInt(KEY_INT, 1);
        storage.setDouble(KEY_DOUBLE, .5);
        storage.setString(KEY_STRING, "test");
        return storage;
    }

}
