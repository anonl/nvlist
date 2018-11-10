package nl.weeaboo.vn.impl.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import nl.weeaboo.vn.core.IEventListener;

public final class EventListenerStub implements IEventListener {

    private final AtomicInteger eventCount = new AtomicInteger();

    @Override
    public void onEvent() {
        eventCount.incrementAndGet();
    }

    /**
     * Resets and checks the internal event counter which is increased every time {@link #onEvent()} is called.
     */
    public void consumeEventCount(int expected) {
        Assert.assertEquals(expected, eventCount.getAndSet(0));
    }

}
