package nl.weeaboo.vn.buildgui;

import java.awt.Color;

/**
 * Listener for logging events.
 */
public interface IBuildLogListener {

    /**
     * Callback that's called when the build process generates a log message.
     *
     * @see #onLogLine(String, Color)
     */
    default void onLogLine(String message) {
        onLogLine(message, Color.BLACK);
    }

    /**
     * Callback that's called when the build process generates a log message.
     */
    void onLogLine(String message, Color color);

}
