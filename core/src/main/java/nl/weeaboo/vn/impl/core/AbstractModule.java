package nl.weeaboo.vn.impl.core;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.render.IRenderEnv;

/**
 * Base implementation of {@link IModule}.
 */
public abstract class AbstractModule implements IModule {

    private static final long serialVersionUID = 1L;

    private boolean destroyed;

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public final boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void update() {
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
    }

    @Override
    public void setRenderEnv(IRenderEnv env) {
    }

    @Override
    public void clearCaches() {
    }

}
