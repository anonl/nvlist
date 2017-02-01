package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.test.integration.lua.LuaIntegrationTest;

public abstract class AbstractLibTest extends LuaIntegrationTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);
    }

}
