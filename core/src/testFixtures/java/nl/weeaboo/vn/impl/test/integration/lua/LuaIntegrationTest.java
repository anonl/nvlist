package nl.weeaboo.vn.impl.test.integration.lua;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.Context;
import nl.weeaboo.vn.impl.core.ContextManager;
import nl.weeaboo.vn.impl.core.EnvironmentFactory;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptThread;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.script.ScriptException;

/** Base class for tests of the Lua script environment */
public abstract class LuaIntegrationTest {

    protected TestEnvironment env;
    protected ContextManager contextManager;
    protected Context mainContext;
    protected ILuaScriptThread mainThread;

    @Before
    public void init() throws ScriptException {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();
        contextManager = env.getContextManager();

        addInitializers(env.getScriptEnv());
        env.getScriptEnv().initEnv();

        // Create an initial context and activate it
        mainContext = (Context)env.createActiveContext();

        mainThread = mainContext.getScriptContext().getMainThread();
    }

    /**
     * @param scriptEnv The current script env used by the environment
     */
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        EnvironmentFactory.registerLuaLibs(env, scriptEnv);

        // Add unit test helper functions
        scriptEnv.addInitializer(new LuaAssertLib());
    }

    @After
    public void deinit() {
        env.destroy();
    }

    protected void loadScript(String path) {
        loadScript(FilePath.of(path));
    }

    protected void loadScript(FilePath path) {
        try {
            LuaScriptUtil.loadScript(mainContext, env.getScriptEnv().getScriptLoader(), path);
        } catch (IOException | ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void runLua(String luaCode) {
        try {
            LuaScriptUtil.eval(mainContext, luaCode);
        } catch (ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void textContinue() {
        buttonPress(VKey.TEXT_CONTINUE);
    }

    protected void buttonPress(VKey pressed) {
        env.getInput().buttonPressed(pressed);
        env.update();
    }

    protected void waitForAllThreads() {
        LuaTestUtil.waitForAllThreads(() -> env);
    }

}
