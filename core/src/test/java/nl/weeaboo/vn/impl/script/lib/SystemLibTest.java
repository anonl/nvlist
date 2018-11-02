package nl.weeaboo.vn.impl.script.lib;

import org.junit.Test;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class SystemLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new SystemLib(env));
    }

    /**
     * {@code System.exit()} calls the global {@code onExit} function.
     */
    @Test
    public void testExit() {
        loadScript("integration/system/exit");

        LuaTestUtil.assertGlobal("exitOk", true);
        LuaTestUtil.assertGlobal("exitError", true);
    }

}
