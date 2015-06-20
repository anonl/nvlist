package nl.weeaboo.vn.script.lua.lib;

import java.io.IOException;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.vn.core.impl.Context;
import nl.weeaboo.vn.core.impl.ContextManager;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.TestEnvironment;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractLibTest {

    protected TestEnvironment env;
    protected Context mainContext;
    protected IScriptThread mainThread;

    @Before
    public void init() throws LuaException {
        env = TestEnvironment.newInstance();

        addInitializers(env.scriptEnv);

        env.scriptEnv.initEnv();

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

    protected void loadScript(String path) throws IOException, ScriptException {
        env.scriptLoader.loadScript(mainThread, path);
    }

}
