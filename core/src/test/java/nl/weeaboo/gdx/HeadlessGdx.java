package nl.weeaboo.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;

import nl.weeaboo.gdx.graphics.MockGL;

public class HeadlessGdx {

    private static boolean initialized;

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        HeadlessNativesLoader.load();
        Gdx.files = new HeadlessFiles();
        Gdx.net = new HeadlessNet();
        Gdx.graphics = new MockGraphics();
        Gdx.gl = MockGL.newInstance();
        Gdx.audio = new MockAudio();
        Gdx.input = new MockInput();
        initialized = true;
    }

}
