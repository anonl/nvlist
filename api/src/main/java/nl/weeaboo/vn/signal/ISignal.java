package nl.weeaboo.vn.signal;

/**
 * Consumable event.
 *
 * @see ISignalHandler
 */
public interface ISignal {

    /**
     * @see #setHandled()
     */
    boolean isHandled();

    /** Marks this signal as handled, meaning it shouldn't be sent to any further signal listeners */
    void setHandled();

    /**
     * Convenience method that returns {@code true} if this signal is unhandled and can be cast to the given type.
     */
    <T> boolean isUnhandled(Class<T> type);

}
