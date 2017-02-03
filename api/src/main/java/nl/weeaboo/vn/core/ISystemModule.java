package nl.weeaboo.vn.core;

import nl.weeaboo.prefsstore.IPreferenceStore;

/** Interface for interacting with hardware and external applications. */
public interface ISystemModule extends IModule {

    /**
     * Note: This method should only be used if {@code #canExit()} returns {@code true}.
     *
     * @param force If {@core true}, skip the exit confirmation dialog.
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
     * Called when the user preferences have changed.
     *
     * @param config An object containing the user preferences.
     */
    void onPrefsChanged(IPreferenceStore config);

    /**
     * @return The system environment data.
     */
    ISystemEnv getSystemEnv();

}
