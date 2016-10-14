package nl.weeaboo.vn.script.impl.lib;

import java.io.IOException;

import org.junit.Test;

import nl.weeaboo.vn.save.impl.SaveModule;
import nl.weeaboo.vn.save.impl.SaveTestUtil;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

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
