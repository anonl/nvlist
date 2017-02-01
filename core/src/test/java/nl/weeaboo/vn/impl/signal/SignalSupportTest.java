package nl.weeaboo.vn.impl.signal;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.impl.signal.SignalSupport;

public class SignalSupportTest {

    private SignalSupport signalSupport;

    private SignalHandlerStub handler1;
    private SignalHandlerStub handler2;

    public SignalSupportTest() {
        signalSupport = new SignalSupport();

        handler1 = new SignalHandlerStub();
        handler2 = new SignalHandlerStub();
    }

    @Test
    public void addRemoveHandlers() {
        // No handlers registered yet, so the signals aren't received
        fireSignal();
        checkReceived(0, 0);

        // Add one of the handlers, that handler receives the signal now
        signalSupport.addSignalHandler(1, handler1);
        fireSignal();
        checkReceived(1, 0);

        // Add the second handler
        signalSupport.addSignalHandler(-1, handler2);
        fireSignal();
        checkReceived(1, 1);

        // Fire another signal, but this time handler2 will consume it before is reaches handler1
        handler2.setHandleAllSignals(true);
        fireSignal();
        checkReceived(0, 1);

        // Now remove handler2 so it can no longer consume the signal
        signalSupport.removeSignalHandler(handler2);
        fireSignal();
        checkReceived(1, 0);

        // Remove handler1 as well
        signalSupport.removeSignalHandler(handler1);
        fireSignal();
        checkReceived(0, 0);
    }

    private void fireSignal() {
        signalSupport.handleSignal(new TestSignal());
    }

    private void checkReceived(int expected1, int expected2) {
        Assert.assertEquals(expected1, handler1.consumeReceived());
        Assert.assertEquals(expected2, handler2.consumeReceived());
    }

}
