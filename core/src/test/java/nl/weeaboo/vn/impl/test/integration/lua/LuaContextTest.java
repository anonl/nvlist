package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

import nl.weeaboo.vn.core.SkipMode;

public class LuaContextTest extends LuaIntegrationTest {

    @Test
    public void testAutoRead() {
        loadScript("integration/context/auto-read");
        env.update();

        // Enable auto-read while waiting in a waitClick()
        mainContext.getSkipState().setSkipMode(SkipMode.AUTO_READ);

        waitForAllThreads();
    }

}
