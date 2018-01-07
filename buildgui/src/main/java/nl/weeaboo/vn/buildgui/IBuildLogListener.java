package nl.weeaboo.vn.buildgui;

public interface IBuildLogListener {

    /**
     * Callback that's called when the build process generates a log message.
     */
    void onLogLine(String message);

}
