package nl.weeaboo.vn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.Iterables;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.gdx.styledtext.GdxFontStore;
import nl.weeaboo.gdx.styledtext.GdxFontUtil;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.core.impl.EnvironmentFactory;
import nl.weeaboo.vn.core.impl.LoggerNotifier;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.TransformablePart;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.image.impl.TextureStore;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.GLRenderer;
import nl.weeaboo.vn.render.impl.RenderStats;
import nl.weeaboo.vn.sound.impl.MusicStore;

public class Launcher extends ApplicationAdapter {

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
	private Vector2 spritePos = new Vector2();

    private Novel novel;
    private GLRenderer renderer;
    private DrawBuffer drawBuffer;
    private BasicPartRegistry pr;
    private int testEntity;

    public Launcher() {
        this("res/");
    }
    public Launcher(String resF) {
        this.resourceFolder = Checks.checkNotNull(resF);
    }

	@Override
	public void create() {
        configureLogger();

        resourceFileSystem = new GdxFileSystem(resourceFolder, true);
        assetManager = new AssetManager(resourceFileSystem);
        Texture.setAssetManager(assetManager);
        frameBufferViewport = new FitViewport(vsize.w, vsize.h);

		updateFrameBuffer();

		screenViewport = new FitViewport(vsize.w, vsize.h);

		batch = new SpriteBatch();
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.finishLoading();

        initNovel();

        sceneEnv = new Scene2dEnv(frameBufferViewport);
        osd = Osd.newInstance();
        debugControls = new DebugControls(sceneEnv);
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

        StaticEnvironment.ASSET_MANAGER.set(assetManager);
        StaticEnvironment.TEXTURE_STORE.set(new TextureStore(StaticEnvironment.TEXTURE_STORE));
        StaticEnvironment.GENERATED_TEXTURE_STORE.set(new GeneratedResourceStore(StaticEnvironment.GENERATED_TEXTURE_STORE));
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
        Entity entity = env.getImageModule().createImage(rootLayer);
        testEntity = entity.getId();
        pr = (BasicPartRegistry)env.getPartRegistry();
        ResourceLoadInfo texLoadInfo = new ResourceLoadInfo("test.jpg");
        TransformablePart transformable = entity.getPart(pr.transformable);
        transformable.setPos(640, 360);
        transformable.setZ((short)-100);
        ImagePart image = entity.getPart(pr.image);
        image.setTexture(env.getImageModule().getTexture(texLoadInfo, false), 5);
	}

    private IFontStore createFontStore() {
        GdxFontStore fontStore = new GdxFontStore();
        try {
            for (BitmapFont font : GdxFontUtil.load("font/DejaVuSerif.ttf", 16, 32)) {
                fontStore.registerFont("DejaVuSerif", EFontStyle.PLAIN, font);
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
		frameBufferViewport.update(frameBuffer.getWidth(), frameBuffer.getHeight(), true);
	}

	@Override
	public final void render() {
		update(Gdx.graphics.getDeltaTime());

		frameBuffer.begin();
        frameBufferViewport.apply();
        Gdx.gl.glClearColor(.514f, .380f, .584f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Camera camera = frameBufferViewport.getCamera();
        batch.setProjectionMatrix(camera.combined);

        renderScreen(batch);
		frameBuffer.end();

        screenViewport.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(screenViewport.getCamera().combined);

		batch.begin();
		batch.draw(frameBuffer.getColorBufferTexture(), 0, vsize.h, vsize.w, -vsize.h);
        batch.end();
	}

	protected void update(float dt) {
		spritePos.x = (spritePos.x + 256 * dt) % vsize.w;
		spritePos.y = (vsize.h / 2) + 128 * MathUtils.cosDeg(spritePos.x);

        debugControls.update(novel);

        Entity entity = novel.getEnv().getContextManager().findEntity(testEntity);
        if (entity != null) {
            debugControls.update(entity.getPart(pr.transformable), entity.getPart(pr.image));
        }

        novel.update();
	}

	protected void renderScreen(SpriteBatch batch) {
        IEnvironment env = novel.getEnv();

        // Render novel
        if (renderer == null) {
            renderer = new GLRenderer(env.getRenderEnv(), new RenderStats());
        }
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        if (drawBuffer == null) {
            drawBuffer = new DrawBuffer(pr);
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

		screenViewport.update(width, height, true);
        IEnvironment env = novel.getEnv();
        env.updateRenderEnv(Rect.of(0, 0, vsize.w, vsize.h), vsize);

        disposeRenderer();
	}

    public Novel getNovel() {
        return novel;
    }

    private static void configureLogger() {
        try {
            InputStream in = NvlTestUtil.class.getResourceAsStream("logging.properties");
            if (in == null) {
                throw new FileNotFoundException();
            }
            try {
                LogManager.getLogManager().readConfiguration(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            LOG.warn("Unable to read logging config", e);
        }
    }

}
