package nl.weeaboo.vn.impl.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.gdx.res.GeneratedResourceStore;
import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;
import nl.weeaboo.vn.gdx.res.TestAssetManager;
import nl.weeaboo.vn.impl.image.GdxTextureStore;
import nl.weeaboo.vn.impl.image.ImageModule;
import nl.weeaboo.vn.impl.input.Input;
import nl.weeaboo.vn.impl.input.InputConfig;
import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.impl.save.SaveModule;
import nl.weeaboo.vn.impl.script.lib.BasicScriptInitializer;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.sound.SoundModule;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.impl.test.TestFileSystem;
import nl.weeaboo.vn.impl.text.TestFontStore;
import nl.weeaboo.vn.impl.text.TextModule;
import nl.weeaboo.vn.impl.video.VideoModule;

public class TestEnvironment extends DefaultEnvironment {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TestEnvironment.class);

    private final TestInputAdapter inputAdapter;

    public TestEnvironment(TestInputAdapter inputAdapter) {
        this.inputAdapter = Checks.checkNotNull(inputAdapter);
    }

    /** Creates a new test environment using the default settings */
    public static TestEnvironment newInstance() {
        LoggerNotifier notifier = new LoggerNotifier();

        final MultiFileSystem fileSystem = TestFileSystem.newInstance();
        final GdxFileSystem gdxFileSystem = new InternalGdxFileSystem("");
        final NovelPrefsStore prefs = new NovelPrefsStore(fileSystem, fileSystem.getWritableFileSystem());

        NativeInput nativeInput = new NativeInput();
        final TestInputAdapter inputAdapter = new TestInputAdapter(nativeInput);
        InputConfig inputConfig;
        try {
            inputConfig = InputConfig.readDefaultConfig();
        } catch (IOException ioe) {
            inputConfig = new InputConfig();
            LOG.warn("Error reading input config", ioe);
        }
        final Input input = new Input(nativeInput, inputConfig);

        StaticEnvironment.NOTIFIER.set(notifier);
        StaticEnvironment.FILE_SYSTEM.set(fileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(fileSystem.getWritableFileSystem());
        StaticEnvironment.PREFS.set(prefs);
        StaticEnvironment.INPUT.set(input);
        StaticEnvironment.SYSTEM_ENV.set(new TestSystemEnv());

        StaticEnvironment.ASSET_MANAGER.set(new TestAssetManager(gdxFileSystem));
        StaticEnvironment.TEXTURE_STORE.set(new GdxTextureStore(StaticEnvironment.TEXTURE_STORE));
        StaticEnvironment.GENERATED_RESOURCES.set(new GeneratedResourceStore(StaticEnvironment.GENERATED_RESOURCES));
        StaticEnvironment.FONT_STORE.set(new TestFontStore());

        TestEnvironment env = new TestEnvironment(inputAdapter);
        env.renderEnv = CoreTestUtil.BASIC_ENV;
        env.resourceLoadLog = new ResourceLoadLogStub();
        env.seenLog = new SeenLog(env);
        env.playTimer = new PlayTimerStub();

        LuaRunState runState = LuaTestUtil.newRunState();
        LuaScriptLoader scriptLoader = LuaTestUtil.newScriptLoader(env);
        LuaScriptEnv scriptEnv = new LuaScriptEnv(runState, scriptLoader);
        scriptEnv.addInitializer(new BasicScriptInitializer(env));
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
    public TestInputAdapter getInputAdapter() {
        return inputAdapter;
    }

    /** Calls update on everything in the environment that needs it (contexts and input adapter). */
    public void update() {
        inputAdapter.updateInput(100);

        contextManager.update();
    }

}
