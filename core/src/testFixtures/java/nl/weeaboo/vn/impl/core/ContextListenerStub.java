package nl.weeaboo.vn.impl.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import nl.weeaboo.vn.core.ContextListener;
import nl.weeaboo.vn.core.IContext;

final class ContextListenerStub extends ContextListener {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger activatedCount = new AtomicInteger();
    private final AtomicInteger deactivatedCount = new AtomicInteger();
    private final AtomicInteger destroyedCount = new AtomicInteger();

    @Override
    public void onContextActivated(IContext context) {
        activatedCount.incrementAndGet();
    }

    @Override
    public void onContextDeactivated(IContext context) {
        deactivatedCount.incrementAndGet();
    }

    @Override
    public void onContextDestroyed(IContext context) {
        destroyedCount.incrementAndGet();
    }

    public void consumeActivatedCount(int expected) {
        Assert.assertEquals(expected, activatedCount.getAndSet(0));
    }

    public void consumeDeactivatedCount(int expected) {
        Assert.assertEquals(expected, deactivatedCount.getAndSet(0));
    }

    public void consumeDestroyedCount(int expected) {
        Assert.assertEquals(expected, destroyedCount.getAndSet(0));
    }

}
