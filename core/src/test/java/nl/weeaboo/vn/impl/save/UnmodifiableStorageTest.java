package nl.weeaboo.vn.impl.save;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.test.ExceptionTester.IExceptionRunnable;
import nl.weeaboo.vn.save.IStorage;

public final class UnmodifiableStorageTest {

    private final ExceptionTester exTester = new ExceptionTester();

    private Storage backing;
    private UnmodifiableStorage unmodifiable;

    @Before
    public void before() {
        backing = new Storage();
        backing.setBoolean("boolean", true);
        backing.setDouble("double", 1.0);
        backing.setInt("int", 1);
        backing.setLong("long", 1L);
        backing.setString("string", "value");

        unmodifiable = UnmodifiableStorage.from(backing);
    }

    /**
     * The {@link UnmodifiableStorage#fromCopy(IStorage)} method returns an independent, immutable copy of the
     * input collection.
     */
    @Test
    public void testFromCopy() {
        UnmodifiableStorage copy = UnmodifiableStorage.fromCopy(backing);

        backing.setInt("int", 2);

        // Check that the copy is independent -- changes to backing don't affect it
        Assert.assertNotEquals(unmodifiable.getInt("int", 0), copy.getInt("int", 0));
    }

    /**
     * Write/modify methods are blocked.
     */
    @Test
    public void testModifyDisallowed() {
        assertUnsupported(() -> unmodifiable.clear());
        assertUnsupported(() -> unmodifiable.remove("test"));
        assertUnsupported(() -> unmodifiable.addAll(new Storage()));
        assertUnsupported(() -> unmodifiable.addAll("prefix", new Storage()));
        assertUnsupported(() -> unmodifiable.set("key", null));
        assertUnsupported(() -> unmodifiable.setBoolean("key", true));
        assertUnsupported(() -> unmodifiable.setDouble("key", 1.0));
        assertUnsupported(() -> unmodifiable.setInt("key", 1));
        assertUnsupported(() -> unmodifiable.setLong("key", 1L));
        assertUnsupported(() -> unmodifiable.setString("key", "value"));
    }

    /**
     * Read methods are delegated to the backing/inner storage.
     */
    @Test
    public void testGetterDelegation() {
        Assert.assertEquals(true, unmodifiable.getBoolean("boolean", false));
        Assert.assertEquals(1.0, unmodifiable.getDouble("double", 0.0), 0.0);
        Assert.assertEquals(1, unmodifiable.getInt("int", 0));
        Assert.assertEquals(1L, unmodifiable.getLong("long", 0L));
        Assert.assertEquals("value", unmodifiable.getString("string", "default"));

        Assert.assertEquals(ImmutableSet.copyOf(backing.getKeys()), ImmutableSet.copyOf(unmodifiable.getKeys()));
        Assert.assertEquals(ImmutableSet.copyOf(backing.getKeys("b")), ImmutableSet.copyOf(unmodifiable.getKeys("b")));
        Assert.assertEquals(backing.contains("boolean"), unmodifiable.contains("boolean"));
        Assert.assertEquals(backing.get("boolean"), unmodifiable.get("boolean"));
    }

    private void assertUnsupported(IExceptionRunnable task) {
        exTester.expect(UnsupportedOperationException.class, task);
    }

}
