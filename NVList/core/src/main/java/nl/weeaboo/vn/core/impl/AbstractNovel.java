package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.save.ISaveModule;

public abstract class AbstractNovel implements INovel {

    // --- Note: This class uses manual serialization ---
    private IEnvironment env;
    // --- Note: This class uses manual serialization ---

    public AbstractNovel(IEnvironment env) {
        this.env = env;
    }

    @Override
    public void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (env != null) {
            env.destroy();
        }

        env = (IEnvironment)in.readObject();
    }

    @Override
    public void writeAttributes(ObjectOutput out) throws IOException {
        out.writeObject(env);
    }

    @Override
    public void start(String mainFunctionName) {
        // Create an initial context and activate it
        IContextManager contextManager = env.getContextManager();
        IContext mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);
    }

    @Override
    public void stop() {
    }

    @Override
    public void update() {
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

}
