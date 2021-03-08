package nl.weeaboo.vn.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.prefsstore.IPreferenceStore;

/**
 * Global preferences change event.
 */
public final class PrefsChangeSignal extends AbstractSignal {

    private final IPreferenceStore prefsStore;

    public PrefsChangeSignal(IPreferenceStore prefsStore) {
        this.prefsStore = Checks.checkNotNull(prefsStore);
    }

    /**
     * Returns the new preferences.
     */
    public IPreferenceStore getPrefsStore() {
        return prefsStore;
    }

}
