package nl.weeaboo.vn.impl.script.lua;

import org.junit.Test;

import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.impl.test.integration.lua.LuaIntegrationTest;

public class LuaPrefsAdapterTest extends LuaIntegrationTest {

    @Test
    public void prefsAccessFromLua() {
        loadScript("prefs-getter.lvn");

        LuaTestUtil.assertGlobal("engineMinVersion", NovelPrefs.ENGINE_MIN_VERSION.getDefaultValue());
    }

}
