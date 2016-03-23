package nl.weeaboo.vn.core.impl;

import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.TestFileSystem;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.image.impl.ImageModule;
import nl.weeaboo.vn.input.impl.NativeInput;
import nl.weeaboo.vn.save.impl.SaveModule;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.impl.lua.LuaScriptLoader;
import nl.weeaboo.vn.script.impl.lua.LuaTestUtil;
import nl.weeaboo.vn.sound.impl.SoundModule;
import nl.weeaboo.vn.text.impl.TextModule;
import nl.weeaboo.vn.video.impl.VideoModule;

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
        StaticEnvironment.INPUT.set(new NativeInput());
        StaticEnvironment.SYSTEM_ENV.set(new TestSystemEnv());

        TestEnvironment env = new TestEnvironment();
        env.renderEnv = CoreTestUtil.BASIC_ENV;
        env.resourceLoadLog = new ResourceLoadLogStub();

        LuaRunState runState = LuaTestUtil.newRunState();
        LuaScriptLoader scriptLoader = LuaTestUtil.newScriptLoader(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        env.scriptEnv = scriptEnv;

        env.saveModule = new SaveModule(env);
        env.imageModule = new ImageModule(env);
        env.soundModule = new SoundModule(env);
        env.textModule = new TextModule();
        env.videoModule = new VideoModule(env);
        env.systemModule = new SystemModuleStub();

        TestContextFactory contextFactory = new TestContextFactory(scriptEnv);
        env.contextManager = new ContextManager(contextFactory);

        return env;
    }

    /** Only valid after calling {@link #newInstance()} */
    public TestSystemEnv getSystemEnv() {
        return (TestSystemEnv)StaticEnvironment.SYSTEM_ENV.get();
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
