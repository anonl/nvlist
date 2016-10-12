package nl.weeaboo.vn.script.impl.lib;

import org.junit.Test;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class SaveLibTest extends AbstractLibTest{

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new SaveLib(env));
    }

    @Test
    public void testGetSaves() {
    }

}
