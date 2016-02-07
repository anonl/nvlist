package nl.weeaboo.vn;

import java.io.IOException;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.Iterables;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.gdx.input.GdxInputAdapter;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.gdx.GdxFontStore;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.impl.EnvironmentFactory;
import nl.weeaboo.vn.core.impl.LoggerNotifier;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.image.impl.TextureStore;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.GLScreenRenderer;
import nl.weeaboo.vn.render.impl.RenderStats;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.sound.impl.MusicStore;

public class Launcher extends ApplicationAdapter {

    static {
        InitConfig.init();
    }

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private final String resourceFolder;

    private GdxFileSystem resourceFileSystem;
    private AssetManager assetManager;
	private FrameBuffer frameBuffer;
	private Dim vsize = new Dim(1280, 720);

	private FitViewport frameBufferViewport;
	private FitViewport screenViewport;

    private Scene2dEnv sceneEnv;
	private Osd osd;
    private DebugControls debugControls;
	private SpriteBatch batch;
    private GdxInputAdapter inputAdapter;

    private Novel novel;
    private GLScreenRenderer renderer;
    private DrawBuffer drawBuffer;

    public Launcher() {
        this("res/");
    }
    public Launcher(String resF) {
        this.resourceFolder = Checks.checkNotNull(resF);
    }

	@Override
	public void create() {
        resourceFileSystem = new GdxFileSystem(resourceFolder, true);
        assetManager = new AssetManager(resourceFileSystem);
        Texture.setAssetManager(assetManager);
        frameBufferViewport = new FitViewport(vsize.w, vsize.h);

		screenViewport = new FitViewport(vsize.w, vsize.h);
        inputAdapter = new GdxInputAdapter(screenViewport);

		batch = new SpriteBatch();
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.finishLoading();

        initNovel();

        sceneEnv = new Scene2dEnv(frameBufferViewport);
        osd = Osd.newInstance();
        debugControls = new DebugControls(sceneEnv);

        Gdx.input.setInputProcessor(new InputMultiplexer(sceneEnv.getStage(), inputAdapter));

        initWindow();
    }

    private void initNovel() {
        IFileSystem inMemoryFileSystem = new InMemoryFileSystem(false);
        MultiFileSystem fileSystem = new MultiFileSystem(resourceFileSystem, inMemoryFileSystem);

        NovelPrefs prefs = new NovelPrefs(fileSystem.getWritableFileSystem());
        try {
            prefs.loadVariables();
            prefs.saveVariables();
        } catch (IOException ioe) {
            LOG.warn("Unable to load variables", ioe);
        }

        StaticEnvironment.NOTIFIER.set(new LoggerNotifier());
        StaticEnvironment.FILE_SYSTEM.set(fileSystem);
        StaticEnvironment.OUTPUT_FILE_SYSTEM.set(fileSystem.getWritableFileSystem());
        StaticEnvironment.PREFS.set(prefs);
        StaticEnvironment.INPUT.set(inputAdapter.getInput());

        StaticEnvironment.ASSET_MANAGER.set(assetManager);
        StaticEnvironment.TEXTURE_STORE.set(new TextureStore(StaticEnvironment.TEXTURE_STORE));
        StaticEnvironment.GENERATED_TEXTURE_STORE.set(new GeneratedResourceStore(StaticEnvironment.GENERATED_TEXTURE_STORE));
        StaticEnvironment.SHADER_STORE.set(new ShaderStore());
        StaticEnvironment.MUSIC_STORE.set(new MusicStore(StaticEnvironment.MUSIC_STORE));
        StaticEnvironment.FONT_STORE.set(createFontStore());

        EnvironmentFactory envFactory = new EnvironmentFactory();
        novel = new Novel(envFactory);
        try {
            novel.start("main");
        } catch (InitException e) {
            LOG.error("Fatal error during init", e);
        }

        // Create a test image
        IEnvironment env = novel.getEnv();
        IContext context = Iterables.get(env.getContextManager().getActiveContexts(), 0);
        ILayer rootLayer = context.getScreen().getRootLayer();

        IImageDrawable image = env.getImageModule().createImage(rootLayer);
        image.setPos(640, 360);
        image.setZ((short)-100);
        image.setTexture(env.getImageModule().getTexture("test"), 5);
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

                BitmapFont[] fonts = GdxFontUtil.load("font/" + name + ".ttf", sizes);
                for (int n = 0; n < fonts.length; n++) {
                    fontStore.registerFont(fontFamily, style, fonts[n], sizes[n]);
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
		osd.dispose();
		batch.dispose();
        assetManager.dispose();
	}

    private void disposeRenderer() {
        if (renderer != null) {
            renderer.destroy();
            renderer = null;
        }
    }

	private void disposeFrameBuffer() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
			frameBuffer = null;
		}
	}

	private void updateFrameBuffer() {
		disposeFrameBuffer();

		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, vsize.w, vsize.h, false);
        frameBufferViewport.update(vsize.w, vsize.h, true);
	}

	@Override
	public final void render() {
        try {
            update();
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }

		frameBuffer.begin();
        frameBufferViewport.apply();
        Gdx.gl.glClearColor(.514f, .380f, .584f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Camera camera = frameBufferViewport.getCamera();
        batch.setProjectionMatrix(camera.combined);

        try {
            renderScreen(batch);
        } catch (RuntimeException re) {
            onUncaughtException(re);
        }
		frameBuffer.end();

        screenViewport.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenViewport.getCamera().combined);

		batch.begin();
		batch.draw(frameBuffer.getColorBufferTexture(), 0, vsize.h, vsize.w, -vsize.h);
        batch.end();
	}

    protected void update() {
        inputAdapter.update();
        IInput input = inputAdapter.getInput();

        debugControls.update(novel, input);

        IEnvironment env = novel.getEnv();
        IContext context = Iterables.getFirst(env.getContextManager().getActiveContexts(), null);
        if (context != null) {
            ILayer rootLayer = context.getScreen().getRootLayer();
            IImageDrawable first = Iterables
                    .getFirst(Iterables.filter(rootLayer.getChildren(), IImageDrawable.class), null);
            if (first != null) {
                debugControls.update(first, input);
            }
        }

        novel.update();
	}

	protected void renderScreen(SpriteBatch batch) {
        IEnvironment env = novel.getEnv();

        // Render novel
        if (renderer == null) {
            renderer = new GLScreenRenderer(env.getRenderEnv(), new RenderStats());
        }
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        if (drawBuffer == null) {
            drawBuffer = new DrawBuffer();
        } else {
            drawBuffer.reset();
        }
        novel.draw(drawBuffer);

        renderer.render(drawBuffer);

        sceneEnv.getStage().draw();

		batch.begin();
        osd.render(batch, env);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

        LOG.info("Viewport resized: ({}x{})", width, height);

        initWindow();
    }

    private void initWindow() {
        screenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        IEnvironment env = novel.getEnv();
        env.updateRenderEnv(Rect.of(0, 0, vsize.w, vsize.h), vsize);

        disposeRenderer();
        updateFrameBuffer();
	}

    public Novel getNovel() {
        return novel;
    }

    private void onUncaughtException(RuntimeException re) {
        LOG.error("Uncaught exception", re);
    }

}
