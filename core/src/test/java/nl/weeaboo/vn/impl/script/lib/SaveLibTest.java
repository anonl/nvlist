package nl.weeaboo.vn.impl.script.lib;

import java.io.IOException;

import org.junit.Test;

import nl.weeaboo.vn.impl.save.SaveModule;
import nl.weeaboo.vn.impl.save.SaveTestUtil;
import nl.weeaboo.vn.impl.script.lib.SaveLib;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;

public class SaveLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SaveLib(env));
    }

    @Test
    public void testGetSaves() throws IOException {
        SaveTestUtil.writeDummySave((SaveModule)env.getSaveModule(), 1);

        loadScript("save/getsaves.lvn");
    }

}
