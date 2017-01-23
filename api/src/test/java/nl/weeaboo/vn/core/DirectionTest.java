package nl.weeaboo.vn.core;

import java.util.Collections;
import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class DirectionTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(1141102430, EnumTester.hashEnum(Direction.class));
    }

    /**
     * Each direction constant has an associated integer. Check that those integers are unique and the mapping
     * is symmetrical.
     */
    @Test
    public void intValues() {
        for (Direction dir : Direction.values()) {
            Direction dir2 = Direction.fromInt(dir.intValue());
            Assert.assertSame(dir2, dir);
        }
    }

    /**
     * Invalid integer values result in {@link Direction#NONE}.
     */
    @Test
    public void fromInvalidValue() {
        Assert.assertSame(Direction.NONE, Direction.fromInt(12345));
        Assert.assertSame(Direction.NONE, Direction.fromInt(-1));
    }

    /**
     * Tests for the {@link Direction#isLeft()} and its equivalents for other directions.
     */
    @Test
    public void simplifiedDirections() {
        assertTop(7, 8, 9);
        assertRight(9, 6, 3);
        assertBottom(1, 2, 3);
        assertLeft(1, 4, 7);
    }

    private void assertLeft(int... exp) {
        EnumSet<Direction> expected = toDirections(exp);
        for (Direction dir : expected) {
            Assert.assertTrue(Direction.containsLeft(Collections.singleton(dir)));
        }
        Assert.assertFalse(Direction.containsLeft(EnumSet.complementOf(expected)));
    }

    private void assertBottom(int... exp) {
        EnumSet<Direction> expected = toDirections(exp);
        for (Direction dir : expected) {
            Assert.assertTrue(Direction.containsBottom(Collections.singleton(dir)));
        }
        Assert.assertFalse(Direction.containsBottom(EnumSet.complementOf(expected)));
    }

    private void assertRight(int... exp) {
        EnumSet<Direction> expected = toDirections(exp);
        for (Direction dir : expected) {
            Assert.assertTrue(Direction.containsRight(Collections.singleton(dir)));
        }
        Assert.assertFalse(Direction.containsRight(EnumSet.complementOf(expected)));
    }

    private void assertTop(int... exp) {
        EnumSet<Direction> expected = toDirections(exp);
        for (Direction dir : expected) {
            Assert.assertTrue(Direction.containsTop(Collections.singleton(dir)));
        }
        Assert.assertFalse(Direction.containsTop(EnumSet.complementOf(expected)));
    }

    private static EnumSet<Direction> toDirections(int... dirInts) {
        EnumSet<Direction> result = EnumSet.noneOf(Direction.class);
        for (int dirInt : dirInts) {
            result.add(Direction.fromInt(dirInt));
        }
        return result;
    }

}
