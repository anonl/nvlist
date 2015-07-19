package nl.weeaboo.vn.core.impl;

import static nl.weeaboo.vn.core.NovelPrefs.HEIGHT;
import static nl.weeaboo.vn.core.NovelPrefs.WIDTH;

import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.image.impl.ImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.impl.SaveModule;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.sound.impl.SoundModule;
import nl.weeaboo.vn.video.impl.VideoModule;

public class NovelBuilder {

    private final AssetManager assetManager;
    private final IPreferenceStore prefs;

    public NovelBuilder(AssetManager assetManager) {
        this.assetManager = Checks.checkNotNull(assetManager);
        this.prefs = StaticEnvironment.PREFS.get();
    }

    public Novel build() throws InitException {
        try {
            DefaultEnvironment env = initEnvironment();
            initScriptState(env);
            return buildNovel(env);
        } catch (RuntimeException re) {
            throw new InitException(re);
        }
    }

    protected DefaultEnvironment initEnvironment() throws InitException {
        DefaultEnvironment env = new DefaultEnvironment();
        initEnvironment(env);
        return env;
    }

    /**
     * @throws InitException If an unrecoverable initialization error occurs.
     */
    protected void initEnvironment(DefaultEnvironment env) throws InitException {
        final Dim vsize = new Dim(prefs.get(WIDTH), prefs.get(HEIGHT));
        RenderEnv renderEnv = RenderEnv.newDefaultInstance(vsize, false);

        env.renderEnv = renderEnv;
        env.partRegistry = new BasicPartRegistry();
        env.systemEventHandler = new SystemEventHandler();

        // Init Lua script env
        LuaRunState runState = new LuaRunState();
        LuaScriptLoader scriptLoader = LuaScriptLoader.newInstance(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);

        ContextFactory contextFactory = new ContextFactory(scriptEnv, renderEnv);
        env.contextManager = new ContextManager(contextFactory);
        env.scriptEnv = scriptEnv;
        env.saveModule = new SaveModule(env);

        // Init modules
        env.imageModule = new ImageModule(env, assetManager);
        env.soundModule = new SoundModule(env);
        env.videoModule = new VideoModule(env);
    }

    protected void initScriptState(DefaultEnvironment env) throws InitException {
     // TODO LVN-017
//      if (isVNDS()) {
//          novel.setBootstrapScripts("builtin/vnds/main.lua");
//      }

        ISaveModule saveModule = env.getSaveModule();
        saveModule.loadPersistent();

        LuaScriptEnv scriptEnv = env.getScriptEnv();
        try {
            scriptEnv.initEnv();
        } catch (LuaException e) {
            throw new InitException(e);
        }
    }

    protected Novel buildNovel(DefaultEnvironment env) {
        return new Novel(env);
    }

    public static class InitException extends Exception {

        private static final long serialVersionUID = 1L;

        public InitException(Throwable cause) {
            this("Fatal error during initialization", cause);
        }

        public InitException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
