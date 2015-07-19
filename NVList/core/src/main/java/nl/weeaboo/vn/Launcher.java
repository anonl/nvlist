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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.Iterables;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.core.impl.LoggerNotifier;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.NovelBuilder;
import nl.weeaboo.vn.core.impl.NovelBuilder.InitException;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.TransformablePart;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.GLRenderer;
import nl.weeaboo.vn.render.impl.RenderStats;

public class Launcher extends ApplicationAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private GdxFileSystem resourceFileSystem;
    private AssetManager assetManager;
	private FrameBuffer frameBuffer;
	private Dim vsize = new Dim(1280, 720);

	private FitViewport frameBufferViewport;
	private FitViewport screenViewport;

	private Osd osd;
    private DebugControls debugControls;
	private SpriteBatch batch;
	private Texture img;
	private Vector2 spritePos = new Vector2();

    private Novel novel;
    private BasicPartRegistry pr;
    private Entity entity;

	@Override
	public void create() {
        configureLogger();

        resourceFileSystem = new GdxFileSystem("res/", true);
        assetManager = new AssetManager(resourceFileSystem);
        Texture.setAssetManager(assetManager);

		osd = Osd.newInstance();
        debugControls = new DebugControls();

		frameBufferViewport = new FitViewport(vsize.w, vsize.h);

		updateFrameBuffer();

		screenViewport = new FitViewport(vsize.w, vsize.h);

		batch = new SpriteBatch();
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.finishLoading();

        img = assetManager.get("badlogic.jpg", Texture.class);

        initNovel();
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

        NovelBuilder novelBuilder = new NovelBuilder(assetManager);
        try {
            novel = novelBuilder.build();
            novel.start("main");
        } catch (InitException e) {
            e.printStackTrace();
        }

        // Create a test image
        IEnvironment env = novel.getEnv();
        IContext context = Iterables.get(env.getContextManager().getActiveContexts(), 0);
        ILayer rootLayer = context.getScreen().getRootLayer();
        entity = env.getImageModule().createImage(rootLayer);
        pr = (BasicPartRegistry)env.getPartRegistry();
        ResourceLoadInfo texLoadInfo = new ResourceLoadInfo("test.jpg");
        TransformablePart transformable = entity.getPart(pr.transformable);
        transformable.setPos(640, 360);
        ImagePart image = entity.getPart(pr.image);
        image.setTexture(env.getImageModule().getTexture(texLoadInfo, false), 5);
	}

	@Override
	public void dispose() {
	    if (novel != null) {
	        novel.stop();
	        novel = null;
	    }

		disposeFrameBuffer();
		osd.dispose();
		batch.dispose();
        assetManager.dispose();
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
		Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		frameBufferViewport.apply();
		Camera camera = frameBufferViewport.getCamera();
        batch.setProjectionMatrix(camera.combined);

		renderScreen(batch);
		frameBuffer.end();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		screenViewport.apply();
		batch.setProjectionMatrix(screenViewport.getCamera().combined);

		batch.begin();
		batch.draw(frameBuffer.getColorBufferTexture(), 0, vsize.h, vsize.w, -vsize.h);
        batch.end();
	}

	protected void update(float dt) {
		spritePos.x = (spritePos.x + 256 * dt) % vsize.w;
		spritePos.y = (vsize.h / 2) + 128 * MathUtils.cosDeg(spritePos.x);

        debugControls.update(novel);
        debugControls.update(entity.getPart(pr.transformable));

        novel.update();
	}

	protected void renderScreen(SpriteBatch batch) {
        // Render novel
        DrawBuffer drawBuffer = new DrawBuffer((BasicPartRegistry)novel.getEnv().getPartRegistry());
        novel.draw(drawBuffer);

        GLRenderer renderer = new GLRenderer(novel.getEnv().getRenderEnv(), new RenderStats());
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.render(drawBuffer);
        renderer.destroy();

		batch.begin();

		batch.draw(img, spritePos.x, spritePos.y);

		osd.render(batch, vsize);

		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		screenViewport.update(width, height, true);
        novel.getEnv().updateRenderEnv(Rect.of(0, 0, vsize.w, vsize.h), vsize);
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
