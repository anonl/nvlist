package nl.weeaboo.vn.script.impl.lib;

import org.junit.Test;

import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.impl.lua.LuaTestUtil;

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
