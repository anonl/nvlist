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

}