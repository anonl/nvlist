package nl.weeaboo.vn.core.impl;

import com.badlogic.gdx.Application.ApplicationType;

import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;

public class SystemModuleStub implements ISystemModule {

    private static final long serialVersionUID = 1L;

    private transient SystemEnv systemEnv;

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
    public ISystemEnv getSystemEnv() {
        if (systemEnv == null) {
            systemEnv = new SystemEnv(ApplicationType.HeadlessDesktop);
        }
        return systemEnv;
    }

}
