package nl.weeaboo.vn.core;

import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.script.ScriptException;

/** Interface for handling events triggered from outside {@link INovel}. */
public interface ISystemEventHandler {

    /** Called when the user attempts to close the app */
    public void onExit() throws ScriptException;

    /**
     * Called when the user preferences have changed.
     * @param config An object containing the user preferences.
     */
    public void onPrefsChanged(IPreferenceStore config);

}
