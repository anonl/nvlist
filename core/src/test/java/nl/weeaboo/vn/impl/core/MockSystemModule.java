package nl.weeaboo.vn.impl.core;

import com.badlogic.gdx.Application.ApplicationType;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;

public class MockSystemModule implements ISystemModule {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    private transient SystemEnv systemEnv;

    public MockSystemModule(IEnvironment env) {
        this.env = env;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public void exit(boolean force) {
        if (!force) {
            SystemModule.callFunction(env, KnownScriptFunctions.ON_EXIT);
        }
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
