package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class InputLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new InputLib());
    }

    // TODO: Implement

}
