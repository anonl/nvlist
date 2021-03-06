package nl.weeaboo.vn.core;

import nl.weeaboo.vn.render.DisplayMode;

/** Interface for interacting with hardware and external applications. */
public interface ISystemModule extends IModule {

    /**
     * Note: This method should only be used if {@code #canExit()} returns {@code true}.
     *
     * @param force If {@code true}, skip the exit confirmation dialog.
     * @see #canExit()
     */
    void exit(boolean force);

    /**
     * @return {@code true} if the application can close itself. This is typically impossible when running in
     *         a mobile or embedded context.
     * @see #exit(boolean)
     */
    boolean canExit();

    /**
     * Clear all internal state and return to the titlescreen.
     *
     * @throws InitException If a fatal error occurs during initialization.
     */
    void restart() throws InitException;

    /**
     * Opens an external browser for the specified URL.
     */
    void openWebsite(String url);

    /**
     * @return The system environment data.
     */
    ISystemEnv getSystemEnv();

    /**
     * Changes the current display mode.
     *
     * @throws IllegalStateException if the given display mode is unsupported.
     * @see ISystemEnv#isDisplayModeSupported(DisplayMode)
     */
    void setDisplayMode(DisplayMode mode);

}
