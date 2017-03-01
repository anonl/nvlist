package nl.weeaboo.vn.impl.core;

import static nl.weeaboo.vn.core.NovelPrefs.HEIGHT;
import static nl.weeaboo.vn.core.NovelPrefs.WIDTH;

import nl.weeaboo.common.Dim;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.impl.image.ImageModule;
import nl.weeaboo.vn.impl.save.SaveModule;
import nl.weeaboo.vn.impl.script.lib.BasicScriptInitializer;
import nl.weeaboo.vn.impl.script.lib.CoreLib;
import nl.weeaboo.vn.impl.script.lib.GuiLib;
import nl.weeaboo.vn.impl.script.lib.ImageFxLib;
import nl.weeaboo.vn.impl.script.lib.ImageLib;
import nl.weeaboo.vn.impl.script.lib.InputLib;
import nl.weeaboo.vn.impl.script.lib.InterpolatorsLib;
import nl.weeaboo.vn.impl.script.lib.LogLib;
import nl.weeaboo.vn.impl.script.lib.SaveLib;
import nl.weeaboo.vn.impl.script.lib.SeenLib;
import nl.weeaboo.vn.impl.script.lib.SoundLib;
import nl.weeaboo.vn.impl.script.lib.SystemLib;
import nl.weeaboo.vn.impl.script.lib.TextLib;
import nl.weeaboo.vn.impl.script.lib.TweenLib;
import nl.weeaboo.vn.impl.script.lib.VideoLib;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.impl.sound.SoundModule;
import nl.weeaboo.vn.impl.text.TextModule;
import nl.weeaboo.vn.impl.video.VideoModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.ScriptException;

public class EnvironmentFactory {

    /**
     * Creates a new {@link IEnvironment} instance.
     *
     * @throws InitException If a fatal error occurs.
     */
    public DefaultEnvironment build() throws InitException {
        try {
            DefaultEnvironment env = initEnvironment();
            initScriptState(env);
            return env;
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
        final Dim vsize = Dim.of(getPref(WIDTH), getPref(HEIGHT));
        RenderEnv renderEnv = RenderEnv.newDefaultInstance(vsize, false);

        env.renderEnv = renderEnv;
        env.seenLog = new SeenLog(env);
        env.resourceLoadLog = new ResourceLoadLog(env.getSeenLog());
        env.playTimer = new PlayTimer();

        // Init Lua script env
        LuaRunState runState = new LuaRunState();
        LuaScriptLoader scriptLoader = LuaScriptLoader.newInstance(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        env.scriptEnv = scriptEnv;

        // Init modules
        env.saveModule = new SaveModule(env);
        env.imageModule = new ImageModule(env);
        env.soundModule = new SoundModule(env);
        env.videoModule = new VideoModule(env);
        env.textModule = new TextModule();
        env.systemModule = new SystemModule(env);

        // Init context
        ContextFactory contextFactory = new ContextFactory(scriptEnv, env.getTextModule(), renderEnv);
        env.contextManager = new ContextManager(contextFactory);
    }

    protected void initScriptState(DefaultEnvironment env) throws InitException {
        LuaScriptEnv scriptEnv = env.getScriptEnv();

        // Register basic types/objects
        scriptEnv.addInitializer(new BasicScriptInitializer(env));

        // Register script libs
        registerLuaLibs(env, scriptEnv);

        // TODO LVN-017
        // if (isVNDS()) {
        //     novel.setBootstrapScripts("builtin/vnds/main.lua");
        // }

        ISaveModule saveModule = env.getSaveModule();
        saveModule.loadPersistent();

        try {
            scriptEnv.initEnv();
        } catch (ScriptException e) {
            throw new InitException(e);
        } catch (RuntimeException e) {
            throw new InitException(e);
        }
    }

    /** Registers all standard Lua libraries with the supplied Lua script environment. */
    public static void registerLuaLibs(IEnvironment env, LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new LogLib());
        scriptEnv.addInitializer(new CoreLib(env));
        scriptEnv.addInitializer(new SeenLib(env));
        scriptEnv.addInitializer(new InterpolatorsLib());
        scriptEnv.addInitializer(new ImageLib(env));
        scriptEnv.addInitializer(new ImageFxLib(env));
        scriptEnv.addInitializer(new TweenLib(env));
        scriptEnv.addInitializer(new SoundLib(env));
        scriptEnv.addInitializer(new InputLib());
        scriptEnv.addInitializer(new TextLib(env, scriptEnv));
        scriptEnv.addInitializer(new SaveLib(env));
        scriptEnv.addInitializer(new GuiLib(env));
        scriptEnv.addInitializer(new SystemLib(env));
        scriptEnv.addInitializer(new VideoLib(env));
    }

    private static IPreferenceStore getPrefs() {
        return StaticEnvironment.PREFS.get();
    }

    protected <T> T getPref(Preference<T> pref) {
        return getPrefs().get(pref);
    }

}
