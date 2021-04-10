package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.integration.lua.LuaAssertLib;

public class CoreLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new CoreLib(env));
        scriptEnv.addInitializer(new LuaAssertLib());
    }

    @Test
    public void createContext() {
        loadScript(LuaTestUtil.SCRIPT_SCRIPTLIB);

        IContext createdContext = LuaTestUtil.getGlobal("ctx", IContext.class);
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
        loadScript("setmode.lvn");

        Assert.assertEquals(2, env.getContextManager().getContexts().size());
        LuaTestUtil.assertGlobal("oldContextTrigger", false);
        LuaTestUtil.assertGlobal("newContextTrigger", true);
    }

    @Test
    public void testScheduleEvent() {
        loadScript("lib/core/schedule-event.lvn");
        LuaTestUtil.assertGlobal("finished", false);

        // The scheduled event runs on the next frame
        contextManager.update();
        LuaTestUtil.assertGlobal("finished", true);
    }

    @Test
    public void testCallInContext() {
        loadScript("lib/core/call-in-context.lvn");
        waitForAllThreads();
    }

}
