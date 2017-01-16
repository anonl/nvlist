package nl.weeaboo.vn.core;

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

}
