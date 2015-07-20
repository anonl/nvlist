package nl.weeaboo.vn.core.impl;

import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.TestFileSystem;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.script.lua.LuaTestUtil;

public class TestEnvironment extends DefaultEnvironment {

    private static final long serialVersionUID = 1L;

    public static TestEnvironment newInstance() {
        LoggerNotifier notifier = new LoggerNotifier();
        MultiFileSystem fileSystem = TestFileSystem.newInstance();
        NovelPrefs prefs = new NovelPrefs(fileSystem.getWritableFileSystem());

        StaticEnvironment.NOTIFIER.set(notifier);
        StaticEnvironment.FILE_SYSTEM.set(fileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(fileSystem.getWritableFileSystem());
        StaticEnvironment.PREFS.set(prefs);

        TestEnvironment env = new TestEnvironment();
        env.partRegistry = new BasicPartRegistry();
        env.renderEnv = NvlTestUtil.BASIC_ENV;
        env.systemEventHandler = new TestSystemEventHandler();
        env.resourceLoadLog = new ResourceLoadLogStub();

        LuaRunState runState = LuaTestUtil.newRunState();
        LuaScriptLoader scriptLoader = LuaTestUtil.newScriptLoader(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        env.scriptEnv = scriptEnv;

        TestContextFactory contextFactory = new TestContextFactory(scriptEnv);
        env.contextManager = new ContextManager(contextFactory);

        return env;
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            super.destroy();

            scriptEnv.getRunState().destroy();
        }
    }

    @Override
    public ContextManager getContextManager() {
        return (ContextManager)super.getContextManager();
    }

}
