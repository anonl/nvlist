package nl.weeaboo.vn.impl.script.lib;

import org.junit.Test;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;

public class SoundLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SoundLib(env));
    }

    @Test
    public void setMasterVolume() {
        loadScript("integration/sound/setmastervolume");
    }

}
