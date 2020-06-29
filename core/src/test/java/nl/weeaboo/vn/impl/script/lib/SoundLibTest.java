package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class SoundLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SoundLib(env));
    }

    @Test
    public void setMasterVolume() {
        loadScript("integration/sound/setmastervolume");

        ISoundController sc = env.getSoundModule().getSoundController();
        Assert.assertEquals(0.1, sc.getMasterVolume(SoundType.MUSIC), 0.0);
        Assert.assertEquals(0.2, sc.getMasterVolume(SoundType.SOUND), 0.0);
        Assert.assertEquals(0.3, sc.getMasterVolume(SoundType.VOICE), 0.0);
    }

}
