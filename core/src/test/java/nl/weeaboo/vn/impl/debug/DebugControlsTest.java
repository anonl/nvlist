package nl.weeaboo.vn.impl.debug;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.NovelMock;
import nl.weeaboo.vn.impl.core.SystemModuleMock;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.impl.input.TestInputAdapter;
import nl.weeaboo.vn.impl.script.lua.LuaConsoleMock;
import nl.weeaboo.vn.input.KeyCode;

public final class DebugControlsTest {

    private NovelMock novel;
    private TestEnvironment env;
    private NativeInput nativeInput;
    private TestInputAdapter inputAdapter;
    private DebugControls debugControls;

    @Before
    public void before() {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();
        novel = new NovelMock(env);

        nativeInput = new NativeInput();
        inputAdapter = new TestInputAdapter(nativeInput);

        debugControls = new DebugControls(new LuaConsoleMock(), new ScreenshotTakerMock());
    }

    @Test
    public void testSaveLoad() {
        setDebugMode(true);

        // Load (save file doesn't exist yet)
        buttonPress(KeyCode.NUMPAD_SUBTRACT);
        update();

        // Save (OK)
        buttonPress(KeyCode.NUMPAD_ADD);
        update();

        // Load (OK)
        buttonPress(KeyCode.NUMPAD_SUBTRACT);
        update();
    }

    @Test
    public void testRestart() {
        SystemModuleMock systemModule = env.getSystemModule();

        // Debug mode disabled: hotkey is ignored
        buttonPress(KeyCode.CONTROL_LEFT, KeyCode.F5);
        update();
        systemModule.consumeRestartCount(0);

        // Debug mode enabled: novel restarts
        setDebugMode(true);
        buttonPress(KeyCode.CONTROL_LEFT, KeyCode.F5);
        update();
        systemModule.consumeRestartCount(1);

        // Restart throws an exception (is logged)
        systemModule.setRestartException(new InitException("test"));
        buttonPress(KeyCode.CONTROL_RIGHT, KeyCode.F5); // Left/right control both work
        update();
        systemModule.consumeRestartCount(1);
    }

    @Test
    public void testClearCaches() {
        // Debug mode disabled: hotkey is ignored
        buttonPress(KeyCode.F3);
        update();
        env.consumeClearCachesCount(0);

        // Debug mode enabled: caches are cleared
        setDebugMode(true);
        buttonPress(KeyCode.F3);
        update();
        env.consumeClearCachesCount(1);
    }

    private void update() {
        debugControls.update(novel, nativeInput);
    }

    private void buttonPress(KeyCode... keys) {
        Stream.of(keys).forEach(inputAdapter::buttonPressed);
        inputAdapter.updateInput();
    }

    private void setDebugMode(boolean enabled) {
        IPreferenceStore prefStore = env.getPrefStore();
        prefStore.set(NovelPrefs.DEBUG, enabled);
    }

}
