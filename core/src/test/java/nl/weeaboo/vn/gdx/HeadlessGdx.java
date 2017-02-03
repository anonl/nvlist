package nl.weeaboo.vn.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;

import nl.weeaboo.vn.gdx.graphics.MockGL;

public class HeadlessGdx {

    /**
     * Initializes a headless GDX platform for testing.
     */
    public static synchronized void init() {
        if (Gdx.gl instanceof MockGL) {
            // Already initialized
            return;
        }

        HeadlessNativesLoader.load();
        Gdx.app = new GdxAppStub();
        Gdx.files = new HeadlessFiles();
        Gdx.net = new HeadlessNet();
        Gdx.graphics = new MockGraphics();
        Gdx.gl20 = MockGL.newInstance();
        Gdx.gl = Gdx.gl20;
        Gdx.audio = new MockAudio();
        Gdx.input = new MockInput();
    }

}
