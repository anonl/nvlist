package nl.weeaboo.vn.signal;

import org.junit.Assert;
import org.junit.Test;

public final class TickSignalTest {

    private final TickSignal signal = new TickSignal();

    @Test
    public void testSetHandled() {
        Assert.assertEquals(false, signal.isHandled());

        signal.setHandled();
        Assert.assertEquals(true, signal.isHandled());
    }

}
