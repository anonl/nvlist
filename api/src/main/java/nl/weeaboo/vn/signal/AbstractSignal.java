package nl.weeaboo.vn.signal;

/**
 * Base implementation of {@link ISignal}.
 */
public abstract class AbstractSignal implements ISignal {

    private boolean handled;

    @Override
    public boolean isHandled() {
        return handled;
    }

    @Override
    public void setHandled() {
        handled = true;
    }

    @Override
    public final <T> boolean isUnhandled(Class<T> type) {
        return !isHandled() && type.isInstance(this);
    }

}