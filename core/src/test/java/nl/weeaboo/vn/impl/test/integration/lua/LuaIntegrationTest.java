package nl.weeaboo.vn.impl.test.integration.lua;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.Context;
import nl.weeaboo.vn.impl.core.ContextManager;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.core.EnvironmentFactory;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.input.TestInputConfig;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

/** Base class for tests of the Lua script environment */
public abstract class LuaIntegrationTest {

    protected TestEnvironment env;
    protected ContextManager contextManager;
    protected Context mainContext;
    protected IScriptThread mainThread;

    @Before
    public void init() throws ScriptException {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();

        addInitializers(env.getScriptEnv());
        env.getScriptEnv().initEnv();

        // Create an initial context and activate it
        contextManager = env.getContextManager();
        mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);
        ContextUtil.setCurrentContext(mainContext);

        IScriptContext mainScriptContext = mainContext.getScriptContext();
        mainThread = mainScriptContext.getMainThread();
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
            env.getScriptEnv().getScriptLoader().loadScript(mainThread, path);
        } catch (IOException | ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void textContinue() {
        env.getInputAdapter().buttonPressed(TestInputConfig.TEXT_CONTINUE);
        env.update();
    }

}
