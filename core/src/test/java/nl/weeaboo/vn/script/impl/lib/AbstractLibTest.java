package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.test.integration.LuaIntegrationTest;

public abstract class AbstractLibTest extends LuaIntegrationTest {

    @Override
    protected abstract void addInitializers(LuaScriptEnv scriptEnv);

}
