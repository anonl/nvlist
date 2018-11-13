package nl.weeaboo.vn.impl.script.lib;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.lua2.vm.LuaInteger;
import nl.weeaboo.vn.impl.save.SaveModule;
import nl.weeaboo.vn.impl.save.SaveTestUtil;
import nl.weeaboo.vn.impl.script.lua.ILuaStorage;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.save.IStorage;

public class SaveLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SaveLib(env));
    }

    @Test
    public void testGetSaves() throws IOException {
        SaveTestUtil.writeDummySave((SaveModule)env.getSaveModule(), 1);

        loadScript("lib/save/getsaves.lvn");
    }

    @Test
    public void testGetSlotIndex() {
        loadScript("lib/save/getslotindex.lvn");

        LuaTestUtil.assertGlobal("quickSave10", 810);
        LuaTestUtil.assertGlobal("autoSave10", 910);
    }

    @Test
    public void testGetSharedGlobals() {
        IStorage sharedGlobals = env.getSaveModule().getSharedGlobals();
        sharedGlobals.setInt("test", 123);

        loadScript("lib/save/sharedglobals.lvn");

        ILuaStorage actual = LuaTestUtil.getGlobal("sharedGlobals", ILuaStorage.class);
        Assert.assertEquals(LuaInteger.valueOf(123), actual.get("test"));
    }

}
