package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.impl.core.SystemEnvMock;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.render.DisplayMode;

public class SystemLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SystemLib(env));
    }

    /**
     * {@code System.exit()} calls the global {@code onExit} function.
     */
    @Test
    public void testExit() {
        loadScript("integration/system/exit");

        LuaTestUtil.assertGlobal("canExit", env.getSystemModule().canExit());
        LuaTestUtil.assertGlobal("exitOk", true);
        LuaTestUtil.assertGlobal("exitError", true);
    }

    @Test
    public void testCompareVersion() {
        loadScript("integration/system/compare-version");
    }

    @Test
    public void testGetTimer() {
        loadScript("integration/system/timer");

        LuaTestUtil.assertGlobal("timer", env.getStatsModule().getPlayTimer());
    }

    @Test
    public void testGetSystemEnv() {
        loadScript("integration/system/system-env");

        LuaTestUtil.assertGlobal("systemEnv", env.getSystemModule().getSystemEnv());
    }

    @Test
    public void testRestartCount() {
        loadScript("integration/system/restart");

        env.getSystemModule().consumeRestartCount(1);
    }

    @Test
    public void testOpenWebsite() {
        loadScript("integration/system/open-website");

        env.getSystemModule().consumeOpenedWebsites("http://example.com");
    }

    @Test
    public void testSetDisplayMode() {
        loadScript("integration/system/set-display-mode");

        SystemEnvMock systemEnv = env.getSystemModule().getSystemEnv();
        Assert.assertEquals(DisplayMode.WINDOWED, systemEnv.getDisplayMode());
    }

}
