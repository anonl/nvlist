package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.test.integration.lua.LuaIntegrationTest;

public abstract class AbstractLibTest extends LuaIntegrationTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);
    }

}
