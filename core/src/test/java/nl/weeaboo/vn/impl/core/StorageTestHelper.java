package nl.weeaboo.vn.impl.core;

import java.util.Set;

import org.junit.Assert;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.impl.save.Storage;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.save.IStorage;

final class StorageTestHelper {

    public static final double EPSILON = CoreTestUtil.EPSILON;

    public static final String KEY_BOOL = "keyBool";
    public static final String KEY_INT = "keyInt";
    public static final String KEY_DOUBLE = "keyDouble";
    public static final String KEY_STRING = "keyString";

    public void assertStorageEquals(IStorage expected, IStorage actual) {
        if (expected == null || actual == null) {
            Assert.assertSame(expected, actual);
            return;
        }

        Set<String> expectedKeys = ImmutableSet.copyOf(expected.getKeys());
        Set<String> actualKeys = ImmutableSet.copyOf(actual.getKeys());
        Assert.assertEquals(expectedKeys, actualKeys);

        for (String key : expectedKeys) {
            Assert.assertEquals(expected.get(key), actual.get(key));
        }
    }

    public void assertBoolean(IStorage storage, String key, boolean expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getBoolean(key, false));
    }

    public void assertInt(IStorage storage, String key, int expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getInt(key, 0));
    }

    public void assertDouble(IStorage storage, String key, double expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getDouble(key, 0), EPSILON);
    }

    public void assertString(IStorage storage, String key, String expected) {
        Assert.assertTrue(storage.contains(key));
        Assert.assertEquals(expected, storage.getString(key, null));
    }

    public Storage createTestData() {
        Storage storage = new Storage();
        storage.setBoolean(KEY_BOOL, true);
        storage.setInt(KEY_INT, 1);
        storage.setDouble(KEY_DOUBLE, .5);
        storage.setString(KEY_STRING, "test");
        return storage;
    }

}
