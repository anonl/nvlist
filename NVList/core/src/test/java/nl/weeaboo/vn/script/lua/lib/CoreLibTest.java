package nl.weeaboo.vn.script.lua.lib;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.CoreLib;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.lua.LuaTestUtil;

public class CoreLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new CoreLib(env));
    }

    @Test
    public void createContext() throws IOException, ScriptException {
        loadScript(LuaTestUtil.SCRIPT_SCRIPTLIB);

        IContext createdContext = LuaTestUtil.getGlobal("context", IContext.class);
        Assert.assertNotNull(createdContext);
        Assert.assertEquals(2, env.getContextManager().getContexts().size());
    }

    @Test
    public void newThread() throws IOException, ScriptException {
        loadScript(LuaTestUtil.SCRIPT_SCRIPTLIB);

        LuaTestUtil.assertGlobal("newThreadResult", 1 + 2 + 3);
    }

}
