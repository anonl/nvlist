package nl.weeaboo.vn.script.impl.lib;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import nl.weeaboo.vn.core.impl.Context;
import nl.weeaboo.vn.core.impl.ContextManager;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.TestEnvironment;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public abstract class AbstractLibTest {

    protected TestEnvironment env;
    protected Context mainContext;
    protected IScriptThread mainThread;

    @Before
    public void init() throws ScriptException {
        env = TestEnvironment.newInstance();

        addInitializers(env.getScriptEnv());
        env.getScriptEnv().initEnv();

        // Create an initial context and activate it
        ContextManager contextManager = env.getContextManager();
        mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);
        ContextUtil.setCurrentContext(mainContext);

        IScriptContext mainScriptContext = mainContext.getScriptContext();
        mainThread = mainScriptContext.getMainThread();
    }

    protected abstract void addInitializers(LuaScriptEnv scriptEvent);

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

}
