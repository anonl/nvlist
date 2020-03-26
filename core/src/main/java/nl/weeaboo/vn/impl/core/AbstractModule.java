package nl.weeaboo.vn.impl.core;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IModule;

/**
 * Base implementation of {@link IModule}.
 */
public abstract class AbstractModule implements IModule {

    private static final long serialVersionUID = 1L;

    @Override
    public void update() {
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
    }

    @Override
    public void destroy() {
    }

}
