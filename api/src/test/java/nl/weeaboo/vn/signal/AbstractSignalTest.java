package nl.weeaboo.vn.signal;

import org.junit.Assert;
import org.junit.Test;

public final class AbstractSignalTest {

    private final TestSignal signal = new TestSignal();

    @Test
    public void testSetHandled() {
        Assert.assertEquals(false, signal.isHandled());

        signal.setHandled();
        Assert.assertEquals(true, signal.isHandled());
    }

    private static final class TestSignal extends AbstractSignal {

    }

}
