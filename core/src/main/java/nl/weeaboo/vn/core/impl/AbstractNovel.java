package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.save.ISaveModule;

public abstract class AbstractNovel implements INovel {

    // --- Note: This class uses manual serialization ---
    private EnvironmentFactory envFactory;
    private IEnvironment env;
    // --- Note: This class uses manual serialization ---

    public AbstractNovel(EnvironmentFactory envFactory) {
        this.envFactory = Checks.checkNotNull(envFactory);
    }

    @Override
    public void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException {
        env = (IEnvironment)in.readObject();
    }

    @Override
    public void writeAttributes(ObjectOutput out) throws IOException {
        out.writeObject(env);
    }

    @Override
    public void start(String mainFuncName) throws InitException {
        StaticEnvironment.NOVEL.set(this);

        // Building the environment also (re)loads persistent data
        env = envFactory.build();

        String engineMinVersion = env.getPref(NovelPrefs.ENGINE_MIN_VERSION);
        String engineTargetVersion = env.getPref(NovelPrefs.ENGINE_TARGET_VERSION);
        try {
            EngineVersion.checkVersion(engineMinVersion, engineTargetVersion);
        } catch (UnsupportedVersionException e) {
            throw new InitException("Incompatible script/engine versions", e);
        }
    }

    @Override
    public void stop() {
        getSaveModule().savePersistent();
    }

    @Override
    public void update() {
        env.getPlayTimer().update();

        for (IModule module : env.getModules()) {
            module.update();
        }

        getContextManager().update();
    }

    @Override
    public IEnvironment getEnv() {
        return env;
    }

    protected IContextManager getContextManager() {
        return env.getContextManager();
    }

    protected ISaveModule getSaveModule() {
        return env.getSaveModule();
    }

    public void onPrefsChanged() {
        env.getSystemModule().onPrefsChanged(env.getPrefStore());
    }

}
