package nl.weeaboo.vn.core;

import org.junit.Test;

public final class UpdateableTest {

    @Test
    public void testEmptyUpdateable() {
        IUpdateable.EMPTY.update(); // Does nothing
    }

}
