package nl.weeaboo.vn.impl.core;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class LruSetTest {

    private static final int MAX_SIZE = 3;

    private LruSet<Value> set;

    @Before
    public void before() {
        set = new LruSet<>(MAX_SIZE);
    }

    /**
     * Check that items are dropped in least-recently-used order when the set overflows.
     */
    @Test
    public void testLruOrder() {
        set.add(Value.A);
        set.add(Value.B);
        set.add(Value.C);
        assertContains(Value.A, Value.B, Value.C);

        set.add(Value.D); // To add this, the set must remove an existing item
        assertContains(Value.B, Value.C, Value.D); // LRU element is dropped

        set.add(Value.B); // Re-add existing element B, this changes the order in which it's removed
        set.add(Value.E); // Add a new element so the LRU element is dropped
        assertContains(Value.D, Value.B, Value.E); // LRU element is dropped (C, not B)
    }

    private void assertContains(Value... expected) {
        EnumSet<Value> expectedSet = Sets.newEnumSet(Arrays.asList(expected), Value.class);
        for (Value value : Value.values()) {
            Assert.assertEquals(expectedSet.contains(value), set.contains(value));
        }
    }

    private enum Value {
        A, B, C, D, E;
    }
}
