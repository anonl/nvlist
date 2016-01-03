package nl.weeaboo.vn.scene.signal;

public interface ISignal {

    /** @see #setHandled() */
    boolean isHandled();

    /** Marks this signal as handled, meaning it shouldn't be sent to any further signal listeners */
    void setHandled();
}
