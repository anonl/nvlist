package nl.weeaboo.vn.impl;

import java.io.IOException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.input.GdxInputAdapter;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.gdx.res.GdxAssetManager;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.impl.core.Destructibles;
import nl.weeaboo.vn.impl.core.EnvironmentFactory;
import nl.weeaboo.vn.impl.core.LoggerNotifier;
import nl.weeaboo.vn.impl.core.Novel;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;
import nl.weeaboo.vn.impl.core.SimulationRateLimiter;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.SystemEnv;
import nl.weeaboo.vn.impl.debug.DebugControls;
import nl.weeaboo.vn.impl.debug.Osd;
import nl.weeaboo.vn.impl.debug.PerformanceMetrics;
import nl.weeaboo.vn.impl.image.GdxTextureStore;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.input.Input;
import nl.weeaboo.vn.impl.input.InputConfig;
import nl.weeaboo.vn.impl.render.DrawBuffer;
import nl.weeaboo.vn.impl.render.GLScreenRenderer;
import nl.weeaboo.vn.impl.render.GdxViewports;
import nl.weeaboo.vn.impl.render.HybridBackBuffer;
import nl.weeaboo.vn.impl.render.IBackBuffer;
import nl.weeaboo.vn.impl.render.RenderStats;
import nl.weeaboo.vn.impl.text.GdxFontStore;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.render.DisplayMode;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.video.IVideo;
import nl.weeaboo.vn.video.IVideoModule;

/**
 * Cross-platform entry point of the application.
 */
@SuppressWarnings("NullableDereference") // TODO: Fix later
public class Launcher extends ApplicationAdapter implements IUpdateable {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private final GdxFileSystem resourceFileSystem;
    private final IWritableFileSystem outputFileSystem;
    private NovelPrefsStore prefs;

    private @Nullable AssetManager assetManager;

    private @Nullable Scene2dEnv sceneEnv;
    private @Nullable Osd osd;
    private @Nullable DebugControls debugControls;
    private @Nullable GdxInputAdapter inputAdapter;
    private @Nullable PerformanceMetrics performanceMetrics;

    private @Nullable Novel novel;
    private @Nullable SimulationRateLimiter simulationRateLimiter;
    private @Nullable GLScreenRenderer renderer;
    private @Nullable DrawBuffer drawBuffer;
    private @Nullable IBackBuffer backBuffer;
    private @Nullable GdxTextureStore textureStore;
    private @Nullable ShaderStore shaderStore;
    private @Nullable GdxFontStore fontStore;
    private boolean windowDirty;

    public Launcher(GdxFileSystem resourceFileSystem, IWritableFileSystem outputFileSystem) {
        this.resourceFileSystem = Checks.checkNotNull(resourceFileSystem);
        this.outputFileSystem = Checks.checkNotNull(outputFileSystem);
    }

    /** Note: This method may be called at any time, even before {@link #create()} */
    public NovelPrefsStore loadPreferences() {
        NovelPrefsStore result = prefs;
        if (result == null) {
            result = new NovelPrefsStore(resourceFileSystem, outputFileSystem);
            try {
                result.loadVariables();
            } catch (IOException ioe) {
                LOG.warn("Unable to load variables", ioe);
            }
            prefs = result;
        }
        return result;
    }

    @Override
    public void create() {
        LOG.info("Launcher.create() start");

        assetManager = new GdxAssetManager(resourceFileSystem);

        NovelPrefsStore prefs = loadPreferences();
        performanceMetrics = new PerformanceMetrics();

        Dim vsize = Dim.of(prefs.get(NovelPrefs.WIDTH), prefs.get(NovelPrefs.HEIGHT));

        GdxViewports viewports = new GdxViewports(vsize);
        backBuffer = new HybridBackBuffer(vsize, viewports);
        inputAdapter = new GdxInputAdapter(viewports.getScreenViewport());

        try {
            initNovel(prefs, createInput(inputAdapter));
        } catch (InitException e) {
            throw new RuntimeException("Fatal error during init", e);
        }

        simulationRateLimiter = new SimulationRateLimiter();
        simulationRateLimiter.setSimulation(this, 60);

        sceneEnv = new Scene2dEnv(resourceFileSystem, viewports.getScene2dViewport());
        osd = new Osd(performanceMetrics);
        debugControls = new DebugControls(sceneEnv);

        Gdx.input.setInputProcessor(new InputMultiplexer(sceneEnv.getStage(), inputAdapter));

        initWindow(Dim.of(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        LOG.info("Launcher.create() end");
    }

    private static Input createInput(GdxInputAdapter inputAdapter) {
        InputConfig inputConfig;
        try {
            inputConfig = InputConfig.readDefaultConfig();
        } catch (IOException ioe) {
            inputConfig = new InputConfig();
            LOG.warn("Error reading input config", ioe);
        }
        return new Input(inputAdapter.getInput(), inputConfig);
    }

    private Novel initNovel(NovelPrefsStore prefs, Input input) throws InitException {
        StaticEnvironment.NOTIFIER.set(new LoggerNotifier());
        StaticEnvironment.FILE_SYSTEM.set(resourceFileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(outputFileSystem);
        StaticEnvironment.PREFS.set(prefs);
        StaticEnvironment.INPUT.set(input);
        StaticEnvironment.SYSTEM_ENV.set(new SystemEnv(Gdx.app.getType()));

        StaticEnvironment.ASSET_MANAGER.set(assetManager);
        StaticEnvironment.TEXTURE_STORE.set(textureStore =
                new GdxTextureStore(StaticEnvironment.TEXTURE_STORE, resourceFileSystem, prefs));
        StaticEnvironment.SHADER_STORE.set(shaderStore = new ShaderStore());
        StaticEnvironment.FONT_STORE.set(fontStore = new GdxFontStore(resourceFileSystem));

        EnvironmentFactory envFactory = new EnvironmentFactory();
        novel = new Novel(envFactory);
        novel.start("main");

        return novel;
    }

    @Override
    public void dispose() {
        if (novel != null) {
            novel.stop();
            novel = null;
        }

        disposeRenderer();
        backBuffer = DisposeUtil.dispose(backBuffer);

        textureStore = Destructibles.destroy(textureStore);
        shaderStore = Destructibles.destroy(shaderStore);
        fontStore = Destructibles.destroy(fontStore);
        assetManager = DisposeUtil.dispose(assetManager);
    }

    private void disposeRenderer() {
        if (renderer != null) {
            renderer.destroy();
            renderer = null;
        }
    }

    @Override
    public final void render() {
        if (windowDirty) {
            applyVSync();
        }

        try {
            simulationRateLimiter.onRender();
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

        SpriteBatch batch = backBuffer.begin();

        try {
            renderScreen(batch);
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

        backBuffer.end();

        try {
            novel.updateInRenderThread();
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

        backBuffer.flip();
    }

    @Override
    public void update() {
        inputAdapter.update();
        INativeInput input = inputAdapter.getInput();

        handleInput(input);

        performanceMetrics.setLogicFps(simulationRateLimiter.getSimulationUpdateRate());

        novel.update();
    }

    protected void handleInput(INativeInput nativeInput) {
        debugControls.update(novel, nativeInput);

        IEnvironment env = novel.getEnv();

        // Fullscreen toggle (if supported)
        ISystemModule systemModule = env.getSystemModule();
        ISystemEnv systemEnv = systemModule.getSystemEnv();
        if (systemEnv.isDisplayModeSupported(DisplayMode.WINDOWED)) {
            DisplayMode dm = systemEnv.getDisplayMode();
            if (nativeInput.isPressed(KeyCode.ALT_LEFT, true) && nativeInput.consumePress(KeyCode.ENTER)) {
                if (dm == DisplayMode.FULL_SCREEN) {
                    dm = DisplayMode.WINDOWED;
                } else {
                    dm = DisplayMode.FULL_SCREEN;
                }
                systemModule.setDisplayMode(dm);

                // GDX clears internal press state, so we should do the same
                nativeInput.clearButtonStates();
            }
        }

        IInput input = StaticEnvironment.INPUT.get();
        osd.update(env, input);
    }

    protected void renderScreen(SpriteBatch batch) {
        IEnvironment env = novel.getEnv();

        // Render novel
        IRenderEnv renderEnv = env.getRenderEnv();
        if (renderer == null) {
            renderer = new GLScreenRenderer(renderEnv, new RenderStats());
        }
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        if (drawBuffer == null) {
            drawBuffer = new DrawBuffer();
        } else {
            drawBuffer.reset();
        }
        novel.draw(drawBuffer);

        renderer.render(drawBuffer);
        sceneEnv.draw();

        IVideo movie = env.getVideoModule().getBlocking();
        if (movie != null) {
            movie.render();
        }

        osd.render(batch, env);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        LOG.info("Viewport resized: ({}x{}), backbuffer=({}x{})",
                width, height,
                Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

        initWindow(Dim.of(width, height));
    }

    private void initWindow(Dim size) {
        LOG.debug("Init window ({}x{})", size.w, size.h);

        IEnvironment env = novel.getEnv();
        backBuffer.setWindowSize(env, size);
        disposeRenderer();

        windowDirty = true;

        // Select image/video folders base on resolution
        IImageModule imageModule = env.getImageModule();
        imageModule.setImageResolution(size);

        IVideoModule videoModule = env.getVideoModule();
        videoModule.setVideoResolution(size);
    }

    private void applyVSync() {
        // On some drivers/platforms, settings vsync only works after the window is made visible.
        Gdx.graphics.setVSync(true);
    }

    /**
     * Returns the global novel object.
     */
    public Novel getNovel() {
        if (novel == null) {
            throw new IllegalStateException("Launcher isn't running");
        }
        return novel;
    }

    private void onUncaughtException(RuntimeException re) {
        LOG.error("Uncaught exception", re);
    }

    /**
     * This method is called when the user attempts to close the window. This method is only called on the
     * desktop, where close events can be cancelled.
     *
     * @return {@code true} if the window should close, {@code false} to cancel.
     */
    public boolean onCloseRequested() {
        if (novel != null) {
            IEnvironment env = novel.getEnv();
            env.getSystemModule().exit(false);
            return false;
        }
        return true;
    }

    /** Returns the global scene2D environment. */
    public Scene2dEnv getSceneEnv() {
        if (sceneEnv == null) {
            throw new IllegalStateException("Launcher isn't running");
        }
        return sceneEnv;
    }

}
