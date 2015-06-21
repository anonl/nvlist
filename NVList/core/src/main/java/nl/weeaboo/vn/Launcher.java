package nl.weeaboo.vn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.impl.LoggerNotifier;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.NovelBuilder;
import nl.weeaboo.vn.core.impl.NovelBuilder.InitException;
import nl.weeaboo.vn.core.impl.StaticEnvironment;

public class Launcher extends ApplicationAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

	private AssetManager manager;
	private FrameBuffer frameBuffer;
	private Dim vsize = new Dim(1280, 720);

	private FitViewport frameBufferViewport;
	private FitViewport screenViewport;

	private Osd osd;
	private SpriteBatch batch;
	private Texture img;
	private Vector2 spritePos = new Vector2();

    private Novel novel;

	@Override
	public void create() {
		manager = new AssetManager();
		manager.setLoader(Texture.class, new TextureLoader(new FileHandleResolver() {
			@Override
			public FileHandle resolve(String fileName) {
				System.out.println(Gdx.files.local("res/" + fileName).file().getAbsolutePath());
				return Gdx.files.local("res/" + fileName);
			}
		}));
		Texture.setAssetManager(manager);

		osd = Osd.newInstance();

		frameBufferViewport = new FitViewport(vsize.w, vsize.h);

		updateFrameBuffer();

		screenViewport = new FitViewport(vsize.w, vsize.h);

		batch = new SpriteBatch();
		manager.load("badlogic.jpg", Texture.class);
		manager.finishLoading();

		img = manager.get("badlogic.jpg", Texture.class);

        initNovel();
    }

    private void initNovel() {
        IFileSystem readFileSystem = new GdxFileSystem("", true);
        IFileSystem inMemoryFileSystem = new InMemoryFileSystem(false);
        MultiFileSystem fileSystem = new MultiFileSystem(readFileSystem, inMemoryFileSystem);

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

        NovelBuilder novelBuilder = new NovelBuilder();
        try {
            novel = novelBuilder.build();
            novel.start("main");
        } catch (InitException e) {
            e.printStackTrace();
        }
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
		manager.dispose();
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

        novel.update();
	}

	protected void renderScreen(SpriteBatch batch) {
		batch.begin();

		batch.draw(img, spritePos.x, spritePos.y);

		osd.render(batch, vsize);

		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		screenViewport.update(width, height, true);
	}

}
