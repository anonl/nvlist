package nl.weeaboo.vn.buildtools.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;

public final class HeadlessGdx {

    /**
     * Initializes a headless GDX platform.
     */
    public static synchronized void init() {
        if (Gdx.files != null) {
            // Already initialized
            return;
        }

        HeadlessNativesLoader.load();
        Gdx.app = null;
        Gdx.files = new HeadlessFiles();
        Gdx.net = new HeadlessNet();
        Gdx.graphics = new MockGraphics();
        Gdx.gl20 = null;
        Gdx.gl = Gdx.gl20;
        Gdx.audio = new MockAudio();
        Gdx.input = new MockInput();
    }

}
