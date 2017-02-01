package nl.weeaboo.vn.impl.signal;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;

public class SignalHandlerStub implements ISignalHandler, Serializable {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger receiveCount = new AtomicInteger();
    private boolean handleAllSignals;

    @Override
    public void handleSignal(ISignal signal) {
        receiveCount.incrementAndGet();

        if (handleAllSignals) {
            signal.setHandled();
        }
    }

    public int consumeReceived() {
        return receiveCount.getAndSet(0);
    }

    public void setHandleAllSignals(boolean handleAllSignals) {
        this.handleAllSignals = handleAllSignals;
    }

}
