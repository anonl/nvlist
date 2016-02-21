package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.settings.IPreferenceStore;

/** Interface for handling events triggered from outside {@link INovel}. */
public interface ISystemEventHandler extends Serializable {

    /**
     * Called when the user attempts to close the app
     */
    void onExit();

    /**
     * Called when the user preferences have changed.
     * @param config An object containing the user preferences.
     */
    void onPrefsChanged(IPreferenceStore config);

}
