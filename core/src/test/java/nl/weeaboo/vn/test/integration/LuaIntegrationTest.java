package nl.weeaboo.vn.test.integration;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import nl.weeaboo.vn.core.impl.Context;
import nl.weeaboo.vn.core.impl.ContextManager;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.EnvironmentFactory;
import nl.weeaboo.vn.core.impl.TestEnvironment;
import nl.weeaboo.vn.input.impl.TestInputConfig;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

/** Base class for tests of the Lua script environment */
public abstract class LuaIntegrationTest {

    protected TestEnvironment env;
    protected ContextManager contextManager;
    protected Context mainContext;
    protected IScriptThread mainThread;

    @Before
    public void init() throws ScriptException {
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
        try {
            env.getScriptLoader().loadScript(mainThread, path);
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void textContinue() {
        env.getInputAdapter().buttonPressed(TestInputConfig.TEXT_CONTINUE);
        env.update();
    }

}
