package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class TextLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new TextLib(env));
    }

    // TODO: Implement

}
