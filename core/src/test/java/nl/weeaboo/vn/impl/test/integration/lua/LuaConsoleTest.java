package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.gdx.input.GdxInputRobot;
import nl.weeaboo.vn.impl.script.lua.LuaConsole;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.integration.IntegrationTest;

@Category(GdxUiTest.class)
public class LuaConsoleTest extends IntegrationTest {

    private LuaConsole console;
    private GdxInputRobot input;

    @Before
    public void before() {
        console = new LuaConsole(launcher.getSceneEnv());
        console.open(getEnv().getContextManager());

        input = new GdxInputRobot(Gdx.input.getInputProcessor());

        Assert.assertEquals(true, console.isVisible());
    }

    @After
    public void after() {
        console.close();
    }

    /** Evaluate a trivial Lua expression */
    @Test
    public void testBasicEval() {
        input.enter("global = 1");
        LuaTestUtil.assertGlobal("global", 1);

        input.enter("global = global + 1");
        LuaTestUtil.assertGlobal("global", 2);

        // Go back in the command history to the previous command
        input.type(Keys.UP);
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

}
