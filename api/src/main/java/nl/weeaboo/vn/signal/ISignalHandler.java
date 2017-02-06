package nl.weeaboo.vn.signal;

public interface ISignalHandler {

    /**
     * Allows the handler to process the given signal.
     */
    void handleSignal(ISignal signal);

}
