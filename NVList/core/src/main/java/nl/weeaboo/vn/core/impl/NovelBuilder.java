package nl.weeaboo.vn.core.impl;

import static nl.weeaboo.vn.core.NovelPrefs.ENGINE_TARGET_VERSION;
import static nl.weeaboo.vn.core.NovelPrefs.HEIGHT;
import static nl.weeaboo.vn.core.NovelPrefs.WIDTH;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.TestContextBuilder;
import nl.weeaboo.vn.TestSystemEventHandler;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.impl.SaveModule;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.script.lvn.ILvnParser;
import nl.weeaboo.vn.script.lvn.LvnParserFactory;

public class NovelBuilder {

    private final IPreferenceStore prefs;
    private final IFileSystem fileSystem;

    public NovelBuilder() {
        this.prefs = StaticEnvironment.PREFS.get();
        this.fileSystem = StaticEnvironment.FILE_SYSTEM.get();
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
        EnvironmentBuilder eb = new EnvironmentBuilder();
        initEnvironment(eb);
        return new DefaultEnvironment(eb);
    }

    /**
     * @throws InitException If an unrecoverable initialization error occurs.
     */
    protected void initEnvironment(EnvironmentBuilder eb) throws InitException {
        final Dim vsize = new Dim(prefs.get(WIDTH), prefs.get(HEIGHT));
        eb.renderEnv = RenderEnv.newDefaultInstance(vsize, false);
        eb.partRegistry = new BasicPartRegistry();
        eb.systemEventHandler = new TestSystemEventHandler();

        // Init Lua script env
        LuaRunState runState = new LuaRunState();
        ILvnParser lvnParser = LvnParserFactory.getParser(prefs.get(ENGINE_TARGET_VERSION));
        LuaScriptLoader scriptLoader = LuaScriptLoader.newInstance(lvnParser, fileSystem);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);

        TestContextBuilder contextBuilder = new TestContextBuilder(scriptEnv);
        eb.contextManager = new ContextManager(contextBuilder);
        eb.scriptEnv = scriptEnv;
        eb.saveModule = new SaveModule(eb);
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
