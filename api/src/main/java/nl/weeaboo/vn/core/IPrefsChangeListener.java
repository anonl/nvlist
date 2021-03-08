package nl.weeaboo.vn.core;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.signal.PrefsChangeSignal;

/**
 * Event listener for {@link IPreferenceStore}.
 *
 * @deprecated Replaced by {@link PrefsChangeSignal}
 */
@Deprecated
public interface IPrefsChangeListener {

    /**
     * Called when the user preferences have changed.
     *
     * @param config An object containing the user preferences.
     */
    void onPrefsChanged(IPreferenceStore config);

}
