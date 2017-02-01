package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.impl.script.lib.CoreLib;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class CoreLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new CoreLib(env));
    }

    @Test
    public void createContext() {
        loadScript(LuaTestUtil.SCRIPT_SCRIPTLIB);

        IContext createdContext = LuaTestUtil.getGlobal("context", IContext.class);
        Assert.assertNotNull(createdContext);
        Assert.assertEquals(2, env.getContextManager().getContexts().size());
    }

    @Test
    public void newThread() {
        loadScript(LuaTestUtil.SCRIPT_SCRIPTLIB);

        LuaTestUtil.assertGlobal("newThreadResult", 1 + 2 + 3);
    }

    @Test
    public void setMode() {
        loadScript(LuaTestUtil.SCRIPT_SETMODE);

        Assert.assertEquals(2, env.getContextManager().getContexts().size());
        LuaTestUtil.assertGlobal("oldContextTrigger", false);
        LuaTestUtil.assertGlobal("newContextTrigger", true);
    }

}
