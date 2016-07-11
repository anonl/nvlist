package nl.weeaboo.vn.signal.impl;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;

/** Convenience class for registering and notifying signal handlers */
public class SignalSupport implements ISignalHandler, Serializable {

    private static final long serialVersionUID = SignalImpl.serialVersionUID;

    /*
     * Copy-on-write behavior is used to prevent ConcurrentModificationException when listeners are
     * added/removed from within the handleSignal method.
     */
    private final List<HandlerEntry> handlers = Lists.newCopyOnWriteArrayList();

    public <T extends ISignalHandler & Serializable> void addSignalHandler(int order, T handler) {
        HandlerEntry newEntry = new HandlerEntry(order, handler);

        int index = 0;
        for (HandlerEntry entry : handlers) {
            if (entry.order > order) {
                handlers.add(index, newEntry);
                return;
            }
            index++;
        }
        handlers.add(newEntry);
    }

    /** Removes the first occurrence of the given signal handler */
    public void removeSignalHandler(ISignalHandler handler) {
        for (HandlerEntry entry : handlers) {
            if (Objects.equal(entry.handler, handler)) {
                handlers.remove(entry);
                return;
            }
        }
    }

    @Override
    public void handleSignal(ISignal signal) {
        for (HandlerEntry entry : handlers) {
            if (signal.isHandled()) {
                break;
            }

            entry.handler.handleSignal(signal);
        }
    }

    private static class HandlerEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int order;
        private final ISignalHandler handler;

        public HandlerEntry(int order, ISignalHandler handler) {
            this.order = order;
            this.handler = Checks.checkNotNull(handler);
        }

    }

}
