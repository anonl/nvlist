package nl.weeaboo.vn.impl.core;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicates;

public final class DestructibleElemListTest {

    private final MockDestructible d1 = new MockDestructible(1);
    private final MockDestructible d2 = new MockDestructible(2);

    private final DestructibleElemList<MockDestructible> list = new DestructibleElemList<>();

    /**
     * Destroyed elements are automatically removed from the list.
     */
    @Test
    public void testRemoveDestroyed() {
        list.add(d1);
        list.add(d2);

        d1.destroy();
        Assert.assertEquals(Arrays.asList(d2), list);
        d2.destroy();
        Assert.assertEquals(Arrays.asList(), list);
    }

    /**
     * Find the first element matching a particular predicate.
     */
    @Test
    public void testFindFirst() {
        // Returns null if not found
        Assert.assertSame(null, list.findFirst(Predicates.alwaysFalse()));

        MockDestructible d3a = new MockDestructible(3);
        MockDestructible d3b = new MockDestructible(3);

        list.add(d1);
        list.add(d2);
        list.add(d3a);
        list.add(d3b);

        Assert.assertSame(d3a, list.findFirst(elem -> elem.getId() == 3));

        d3a.destroy();

        Assert.assertSame(d3b, list.findFirst(elem -> elem.getId() == 3));
    }

    @Test
    public void testDestroyAll() {
        list.add(d1);
        list.add(d2);
        list.destroyAll();

        // List is empty (all elements destroyed)
        Assert.assertEquals(Arrays.asList(), list);
        Assert.assertEquals(true, d1.isDestroyed());
        Assert.assertEquals(true, d2.isDestroyed());
    }

    /**
     * Trying to add an already destroyed element is a no-op.
     */
    @Test
    public void addAlreadyDestroyed() {
        d1.destroy();
        list.add(d1);

        // List is still empty
        Assert.assertEquals(true, list.isEmpty());
    }

}
