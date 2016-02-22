package nl.weeaboo.vn.core.impl;

import com.badlogic.gdx.Application.ApplicationType;

import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;

public class TestSystemModule implements ISystemModule {

    private static final long serialVersionUID = 1L;

    private final SystemEnv systemEnv = new SystemEnv(ApplicationType.HeadlessDesktop);

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public void exit(boolean force) {
    }

    @Override
    public boolean canExit() {
        return false;
    }

    @Override
    public void restart() {
    }

    @Override
    public void openWebsite(String url) {
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
    }

    @Override
    public ISystemEnv getEnvironment() {
        return systemEnv;
    }

}
