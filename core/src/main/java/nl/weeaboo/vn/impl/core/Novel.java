package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.script.IScriptContext;

/**
 * Default implementation of {@link INovel}.
 */
public class Novel implements INovel {

    private static final Logger LOG = LoggerFactory.getLogger(Novel.class);

    // --- Note: This class uses manual serialization ---
    private EnvironmentFactory envFactory;
    private IEnvironment env;
    private transient boolean isStarted;
    // --- Note: This class uses manual serialization ---

    public Novel(EnvironmentFactory envFactory) {
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
    public void start(String mainFunctionName) throws InitException {
        doStart(mainFunctionName);

        // Stop execution if requested
        if (env.getPref(NovelPrefs.STOP_ON_ENTRY)) {
            LOG.info("Stopping execution (requested via {})", NovelPrefs.STOP_ON_ENTRY.getKey());
            for (IContext context : getContextManager().getContexts()) {
                IScriptContext scriptContext = context.getScriptContext();
                scriptContext.getMainThread().pause();
            }
        }
    }

    private void doStart(String mainFunctionName) throws InitException {
        StaticEnvironment.NOVEL.set(this);

        // Building the environment also (re)loads persistent data
        env = envFactory.build();

        LOG.info("NVList version {}", EngineVersion.getEngineVersionString());
        String engineMinVersion = env.getPref(NovelPrefs.ENGINE_MIN_VERSION);
        String engineTargetVersion = env.getPref(NovelPrefs.ENGINE_TARGET_VERSION);
        try {
            EngineVersion.checkVersion(engineMinVersion, engineTargetVersion);
        } catch (UnsupportedVersionException e) {
            throw new InitException("Incompatible script/engine versions", e);
        }

        isStarted = true;

        // Create an initial context and activate it
        ContextManager contextManager = getContextManager();
        Context mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);

        // Load main script and call main function
        try {
            LuaScriptUtil.loadScript(mainContext, FilePath.of("main"));
            LuaScriptUtil.callFunction(mainContext, mainFunctionName);
        } catch (Exception e) {
            LOG.warn("Error executing main function: \"{}\"", mainFunctionName, e);
        }
    }

    @Override
    public void restart() throws InitException {
        stop();

        env.clearCaches();

        doStart(KnownScriptFunctions.TITLESCREEN);
    }

    @Override
    public void stop() {
        if (!isStarted) {
            return;
        }
        isStarted = false;

        try {
            env.getPrefStore().saveVariables();
        } catch (Exception e) {
            LOG.warn("Error saving preferences", e);
        }

        env.getSaveModule().savePersistent();

        // Stop all modules and clean up their resources
        env.destroy();
    }

    @Override
    public void update() {
        getEnv().update();
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        getContextManager().draw(drawBuffer);
    }

    @Override
    public void updateInRenderThread() {
        for (Context context : getContextManager().getContexts()) {
            context.updateInRenderThread();
        }
    }

    @Override
    public IEnvironment getEnv() {
        return env;
    }

    protected ContextManager getContextManager() {
        return (ContextManager)env.getContextManager();
    }

    protected LuaScriptEnv getScriptEnv() {
        return (LuaScriptEnv)getEnv().getScriptEnv();
    }

}
