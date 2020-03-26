package nl.weeaboo.vn.signal;

/**
 * Event handler
 */
public interface ISignalHandler {

    /**
     * Allows the handler to process the given signal.
     */
    void handleSignal(ISignal signal);

}
