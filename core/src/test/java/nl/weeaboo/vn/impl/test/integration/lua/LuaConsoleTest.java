package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.input.GdxInputRobot;
import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.impl.input.TestInputAdapter;
import nl.weeaboo.vn.impl.script.lua.LuaConsole;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.integration.IntegrationTest;
import nl.weeaboo.vn.input.KeyCode;

@Category(GdxUiTest.class)
public class LuaConsoleTest extends IntegrationTest {

    private LuaConsole console;
    private GdxInputRobot input;

    @Before
    public void before() {
        setDebugMode(true);

        console = new LuaConsole(launcher.getSceneEnv());
        console.open(getEnv().getContextManager());

        input = new GdxInputRobot(Gdx.input.getInputProcessor());

        Assert.assertEquals(true, console.isVisible());
    }

    @After
    public void after() {
        console.close();
    }

    /** Open/close the console */
    @Test
    public void testToggleVisible() {
        NativeInput nativeInput = new NativeInput();
        TestInputAdapter inputAdapter = new TestInputAdapter(nativeInput);

        Assert.assertEquals(true, console.isVisible());

        inputAdapter.buttonPressed(KeyCode.F1);
        inputAdapter.updateInput();
        console.update(getEnv(), nativeInput);
        Assert.assertEquals(false, console.isVisible());

        // Unable to open LuaConsole outside debug mdoe
        setDebugMode(false);

        inputAdapter.buttonPressed(KeyCode.F1);
        inputAdapter.updateInput();
        console.update(getEnv(), nativeInput);
        Assert.assertEquals(false, console.isVisible());

        setDebugMode(true);

        inputAdapter.buttonPressed(KeyCode.F1);
        inputAdapter.updateInput();
        console.update(getEnv(), nativeInput);
        Assert.assertEquals(true, console.isVisible());
    }

    /** Evaluate a trivial Lua expression */
    @Test
    public void testBasicEval() {
        // Empty command is a no-op
        input.enter("");
        LuaTestUtil.assertGlobal("global", null);

        input.enter("global = 1");
        LuaTestUtil.assertGlobal("global", 1);

        input.enter("global = global + 1");
        LuaTestUtil.assertGlobal("global", 2);

        // Go back in the command history to the previous command
        input.type(Keys.UP); // One command ago
        input.type(Keys.DOWN); // Zero commands ago

        input.type(Keys.UP); // One command ago
        input.type(Keys.UP); // Two commands ago
        input.type(Keys.UP); // Still two commands ago (the furthest we can go back)
        input.type(Keys.DOWN); // One command ago

        // Run previous command again
        input.enter();
        LuaTestUtil.assertGlobal("global", 3);
    }

    /** Attempt to evaluate a syntactically invalid exception */
    @Test
    public void evalInvalidSyntax() {
        // No exception is thrown (a message is printed to the console)
        input.enter("global = %$#%#%");
    }

    /** Hide LuaConsole when F1 is pressed */
    @Test
    public void hideButton() {
        input.type(Keys.F1);

        Assert.assertEquals(false, console.isVisible());
    }

    private void setDebugMode(boolean enabled) {
        IPreferenceStore prefStore = getEnv().getPrefStore();
        prefStore.set(NovelPrefs.DEBUG, enabled);
    }

}
