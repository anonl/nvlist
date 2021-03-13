package nl.weeaboo.vn.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;

import nl.weeaboo.vn.gdx.graphics.MockGL;

/**
 * Initializes global libGDX state for use in a headless (test) environment.
 */
public class HeadlessGdx {

    /**
     * Initializes a headless GDX platform for testing.
     */
    public static synchronized void init() {
        if (Gdx.app != null) {
            // Already initialized
            return;
        }

        HeadlessNativesLoader.load();
        Gdx.app = new GdxAppStub();
        Gdx.files = new HeadlessFiles();
        Gdx.net = new HeadlessNet(new HeadlessApplicationConfiguration());
        Gdx.graphics = new HeadlessGraphics();
        Gdx.gl20 = MockGL.newInstance();
        Gdx.gl = Gdx.gl20;
        Gdx.audio = new GdxAudioMock();
        Gdx.input = new MockInput();
    }

    public static synchronized void clear() {
        Gdx.app = null;
        Gdx.files = null;
        Gdx.net = null;
        Gdx.graphics = null;
        Gdx.gl20 = null;
        Gdx.gl = null;
        Gdx.audio = null;
        Gdx.input = null;
    }

    private static final class HeadlessGraphics extends MockGraphics {

        @Override
        public int getWidth() {
            return 640;
        }

        @Override
        public int getHeight() {
            return 480;
        }

    }

}
