package nl.weeaboo.vn.impl.script.lib;

import org.junit.Test;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class InputLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new InputLib());
    }

    @Test
    public void testConsumeButton() {
        textContinue();

        loadScript("integration/input/consumeinput.lvn");

        LuaTestUtil.assertGlobal("pressed", true);
    }

}
