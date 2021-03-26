package nl.weeaboo.vn.impl.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.gdx.res.AssetManagerMock;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.impl.image.GdxTextureStore;
import nl.weeaboo.vn.impl.image.ImageModule;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.input.InputMock;
import nl.weeaboo.vn.impl.save.SaveModule;
import nl.weeaboo.vn.impl.script.lib.BasicScriptInitializer;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.sound.SoundModule;
import nl.weeaboo.vn.impl.stats.PlayTimerStub;
import nl.weeaboo.vn.impl.stats.StatsModule;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.impl.test.FileSystemMock;
import nl.weeaboo.vn.impl.text.GdxFontStore;
import nl.weeaboo.vn.impl.text.TextModule;
import nl.weeaboo.vn.impl.video.VideoModule;

/**
 * Implementation of {@link IEnvironment} for use in tests.
 */
public class TestEnvironment extends DefaultEnvironment {

    private static final long serialVersionUID = 1L;

    private final InputMock input;
    private final AtomicInteger clearCachesCount = new AtomicInteger();

    public TestEnvironment(InputMock input) {
        this.input = Checks.checkNotNull(input);
    }

    /** Creates a new test environment using the default settings */
    public static TestEnvironment newInstance() {
        LoggerNotifier notifier = new LoggerNotifier();

        final MultiFileSystem fileSystem = FileSystemMock.newInstance();
        final GdxFileSystem gdxFileSystem = FileSystemMock.newGdxFileSystem();
        final NovelPrefsStore prefs = new NovelPrefsStore(fileSystem, fileSystem.getWritableFileSystem());
        final InputMock input = new InputMock();

        StaticEnvironment.NOTIFIER.set(notifier);
        StaticEnvironment.FILE_SYSTEM.set(fileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(fileSystem.getWritableFileSystem());
        StaticEnvironment.PREFS.set(prefs);
        StaticEnvironment.INPUT.set(input);
        StaticEnvironment.SYSTEM_ENV.set(new SystemEnvMock());

        StaticEnvironment.ASSET_MANAGER.set(new AssetManagerMock(gdxFileSystem));
        StaticEnvironment.TEXTURE_STORE.set(new GdxTextureStore(StaticEnvironment.TEXTURE_STORE,
                gdxFileSystem, prefs));
        StaticEnvironment.SHADER_STORE.set(new ShaderStore());
        StaticEnvironment.FONT_STORE.set(new GdxFontStore(gdxFileSystem));

        TestEnvironment env = new TestEnvironment(input);
        env.renderEnv = CoreTestUtil.BASIC_ENV;
        env.statsModule = new StatsModule(env, new PlayTimerStub());

        LuaRunState runState = LuaTestUtil.newRunState();
        LuaScriptLoader scriptLoader = LuaTestUtil.newScriptLoader(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        scriptEnv.addInitializer(new BasicScriptInitializer(env));
        env.scriptEnv = scriptEnv;

        env.saveModule = new SaveModule(env);
        env.imageModule = new ImageModule(env);
        env.soundModule = new SoundModule(env);
        env.textModule = new TextModule(env);
        env.videoModule = new VideoModule(env);
        env.systemModule = new SystemModuleMock(env);

        ContextFactoryMock contextFactory = new ContextFactoryMock(scriptEnv);
        env.contextManager = new ContextManager(contextFactory);

        return env;
    }

    /** Only valid after calling {@link #newInstance()} */
    public SystemEnvMock getSystemEnv() {
        return (SystemEnvMock)StaticEnvironment.SYSTEM_ENV.get();
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            super.destroy();

            scriptEnv.getRunState().destroy();

            StaticEnvironment.ASSET_MANAGER.get().dispose();

            StaticEnvironment.getInstance().clear();
        }
    }

    @Override
    public ContextManager getContextManager() {
        return (ContextManager)super.getContextManager();
    }

    /**
     * @return An input adapter that may be used to generate dummy input during testing.
     */
    public InputMock getInput() {
        return input;
    }

    @Override
    public SystemModuleMock getSystemModule() {
        return (SystemModuleMock)super.getSystemModule();
    }

    /** Calls update on everything in the environment that needs it (contexts and input adapter). */
    @Override
    public void update() {
        input.increaseTime(100);

        super.update();
    }

    @Override
    public void clearCaches() {
        clearCachesCount.incrementAndGet();

        super.clearCaches();
    }

    public void consumeClearCachesCount(int expected) {
        Assert.assertEquals(expected, clearCachesCount.getAndSet(0));
    }
}
