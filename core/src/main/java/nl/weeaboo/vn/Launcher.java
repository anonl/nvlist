package nl.weeaboo.vn;

import java.io.IOException;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.gdx.graphics.ColorTextureLoader;
import nl.weeaboo.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.gdx.graphics.JngTextureLoader;
import nl.weeaboo.gdx.graphics.PremultTextureLoader;
import nl.weeaboo.gdx.input.GdxInputAdapter;
import nl.weeaboo.gdx.res.DisposeUtil;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.prefsstore.IPreferenceListener;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.gdx.GdxFontGenerator;
import nl.weeaboo.styledtext.gdx.GdxFontInfo;
import nl.weeaboo.styledtext.gdx.GdxFontStore;
import nl.weeaboo.styledtext.gdx.YDir;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.impl.EnvironmentFactory;
import nl.weeaboo.vn.core.impl.LoggerNotifier;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.NovelPrefsStore;
import nl.weeaboo.vn.core.impl.SimulationRateLimiter;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.SystemEnv;
import nl.weeaboo.vn.debug.DebugControls;
import nl.weeaboo.vn.debug.Osd;
import nl.weeaboo.vn.debug.PerformanceMetrics;
import nl.weeaboo.vn.image.impl.GdxTextureStore;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.impl.Input;
import nl.weeaboo.vn.input.impl.InputConfig;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.GLScreenRenderer;
import nl.weeaboo.vn.render.impl.RenderStats;
import nl.weeaboo.vn.sound.impl.GdxMusicStore;
import nl.weeaboo.vn.video.IVideo;

public class Launcher extends ApplicationAdapter implements IUpdateable {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private final GdxFileSystem resourceFileSystem;
    private final IWritableFileSystem outputFileSystem;

    private AssetManager assetManager;
    private FrameBuffer frameBuffer;
    private Dim vsize = Dim.of(1280, 720);

    private FitViewport frameBufferViewport;
    private FitViewport screenViewport;
    private FitViewport scene2dViewport;

    private Scene2dEnv sceneEnv;
    private Osd osd;
    private DebugControls debugControls;
    private SpriteBatch batch;
    private GdxInputAdapter inputAdapter;
    private PerformanceMetrics performanceMetrics;

    private Novel novel;
    private SimulationRateLimiter simulationRateLimiter;
    private GLScreenRenderer renderer;
    private DrawBuffer drawBuffer;
    private boolean windowDirty;

    public Launcher(GdxFileSystem resourceFileSystem, IWritableFileSystem outputFileSystem) {
        this.resourceFileSystem = Checks.checkNotNull(resourceFileSystem);
        this.outputFileSystem = Checks.checkNotNull(outputFileSystem);
    }

    /** Note: This method may be called at any time, even before {@link #create()} */
    public NovelPrefsStore loadPreferences() {
        NovelPrefsStore prefs = new NovelPrefsStore(resourceFileSystem, outputFileSystem);
        try {
            prefs.loadVariables();
        } catch (IOException ioe) {
            LOG.warn("Unable to load variables", ioe);
        }
        return prefs;
    }

    @Override
    public void create() {
        LOG.info("Launcher.create() start");

        assetManager = new AssetManager(resourceFileSystem);
        PremultTextureLoader.register(assetManager);
        ColorTextureLoader.register(assetManager);
        JngTextureLoader.register(assetManager);
        Texture.setAssetManager(assetManager);

        NovelPrefsStore prefs = loadPreferences();
        vsize = Dim.of(prefs.get(NovelPrefs.WIDTH), prefs.get(NovelPrefs.HEIGHT));

        performanceMetrics = new PerformanceMetrics();

        frameBufferViewport = new FitViewport(vsize.w, vsize.h);
        screenViewport = new FitViewport(vsize.w, vsize.h);
        scene2dViewport = new FitViewport(vsize.w, vsize.h);

        // TODO: Input adapter wants a transform from screen to world (y-down)
        inputAdapter = new GdxInputAdapter(screenViewport);

        batch = new SpriteBatch();

        try {
            initNovel(prefs);
        } catch (InitException e) {
            throw new RuntimeException("Fatal error during init", e);
        }

        simulationRateLimiter = new SimulationRateLimiter();
        simulationRateLimiter.setSimulation(this, 60);

        sceneEnv = new Scene2dEnv(resourceFileSystem, scene2dViewport);
        osd = Osd.newInstance(resourceFileSystem, performanceMetrics);
        debugControls = new DebugControls(sceneEnv);

        Gdx.input.setInputProcessor(new InputMultiplexer(sceneEnv.getStage(), inputAdapter));

        initWindow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        LOG.info("Launcher.create() end");
    }

    private void initNovel(NovelPrefsStore prefs) throws InitException {
        InputConfig inputConfig;
        try {
            inputConfig = InputConfig.readDefaultConfig();
        } catch (IOException ioe) {
            inputConfig = new InputConfig();
            LOG.warn("Error reading input config", ioe);
        }
        Input input = new Input(inputAdapter.getInput(), inputConfig);

        StaticEnvironment.NOTIFIER.set(new LoggerNotifier());
        StaticEnvironment.FILE_SYSTEM.set(resourceFileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(outputFileSystem);
        StaticEnvironment.PREFS.set(prefs);
        StaticEnvironment.INPUT.set(input);
        StaticEnvironment.SYSTEM_ENV.set(new SystemEnv(Gdx.app.getType()));

        StaticEnvironment.ASSET_MANAGER.set(assetManager);
        StaticEnvironment.TEXTURE_STORE.set(new GdxTextureStore(StaticEnvironment.TEXTURE_STORE));
        StaticEnvironment.GENERATED_RESOURCES.set(new GeneratedResourceStore(StaticEnvironment.GENERATED_RESOURCES));
        StaticEnvironment.SHADER_STORE.set(new ShaderStore());
        StaticEnvironment.MUSIC_STORE.set(new GdxMusicStore(StaticEnvironment.MUSIC_STORE));
        StaticEnvironment.FONT_STORE.set(createFontStore());

        EnvironmentFactory envFactory = new EnvironmentFactory();
        novel = new Novel(envFactory);

        novel.start("main");

        // Attach listener to static environment
        prefs.addPreferenceListener(new IPreferenceListener() {
            @Override
            public <T> void onPreferenceChanged(Preference<T> pref, T oldValue, T newValue) {
                onPrefsChanged();
            }
        });
    }

    private IFontStore createFontStore() {
        GdxFontStore fontStore = new GdxFontStore();
        try {
            String fontFamily = "RobotoSlab";
            int[] sizes = { 16, 32 };
            for (EFontStyle style : EnumSet.of(EFontStyle.PLAIN, EFontStyle.BOLD, EFontStyle.ITALIC)) {
                String name = fontFamily;
                if (style.isBold()) name += "Bold";
                if (style.isItalic()) name += "Oblique";

                FileHandle fileHandle = resourceFileSystem.resolve("font/" + name + ".ttf");

                MutableTextStyle ts = new MutableTextStyle();
                ts.setFontName(name);
                ts.setFontStyle(style);

                GdxFontGenerator fontGenerator = new GdxFontGenerator();
                fontGenerator.setYDir(YDir.DOWN);
                GdxFontInfo[] fonts = fontGenerator.load(fileHandle, ts.immutableCopy(), sizes);
                for (int n = 0; n < fonts.length; n++) {
                    fontStore.registerFont(fonts[n]);
                }
            }
        } catch (IOException ioe) {
            LOG.warn("Unable to load font(s)", ioe);
        }
        return fontStore;
    }

    @Override
    public void dispose() {
        if (novel != null) {
            novel.stop();
            novel = null;
        }

        disposeRenderer();
        disposeFrameBuffer();
        osd = DisposeUtil.dispose(osd);
        batch = DisposeUtil.dispose(batch);
        assetManager = DisposeUtil.dispose(assetManager);
    }

    private void disposeRenderer() {
        if (renderer != null) {
            renderer.destroy();
            renderer = null;
        }
    }

    private void disposeFrameBuffer() {
        frameBuffer = DisposeUtil.dispose(frameBuffer);
    }

    private void updateFrameBuffer() {
        disposeFrameBuffer();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, vsize.w, vsize.h, false);
        GdxViewportUtil.setToOrtho(frameBufferViewport, vsize, true);
        frameBufferViewport.update(vsize.w, vsize.h, true);
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

        frameBuffer.begin();
        frameBufferViewport.apply();
        // Gdx.gl.glClearColor(.514f, .380f, .584f, 1);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Camera camera = frameBufferViewport.getCamera();
        batch.setProjectionMatrix(camera.combined);

        try {
            renderScreen(batch);
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

        frameBuffer.end();

        try {
            novel.updateInRenderThread();
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

        screenViewport.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenViewport.getCamera().combined);

        batch.begin();
        batch.disableBlending();
        try {
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, vsize.w, vsize.h);
        } finally {
            batch.enableBlending();
            batch.end();
        }
    }

    @Override
    public void update() {
        inputAdapter.update();
        INativeInput input = inputAdapter.getInput();

        debugControls.update(novel, input);

        performanceMetrics.setLogicFps(simulationRateLimiter.getSimulationUpdateRate());
        osd.update(input);

        novel.update();
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
            movie.setRenderEnv(renderEnv);
            movie.render();
        }

        osd.render(batch, env);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        LOG.info("Viewport resized: ({}x{})", width, height);

        initWindow(width, height);
    }

    private void initWindow(int width, int height) {
        LOG.info("Init window");

        GdxViewportUtil.setToOrtho(screenViewport, vsize, true);
        screenViewport.update(width, height, true);

        scene2dViewport.update(width, height, true);

        IEnvironment env = novel.getEnv();
        env.updateRenderEnv(Rect.of(0, 0, vsize.w, vsize.h), vsize);

        disposeRenderer();
        updateFrameBuffer();
        windowDirty = true;
    }

    private void applyVSync() {
        // On some drivers/platforms, settings vsync only works after the window is made visible.
        Gdx.graphics.setVSync(true);
    }

    public Novel getNovel() {
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

    private void onPrefsChanged() {
        if (novel != null) {
            novel.onPrefsChanged();
        }
    }

    public Scene2dEnv getSceneEnv() {
        return sceneEnv;
    }

}
