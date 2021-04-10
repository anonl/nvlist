package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.input.VKey;

public class LuaContextTest extends LuaIntegrationTest {

    @Test
    public void testAutoRead() {
        loadScript("integration/context/auto-read");
        env.update();

        // Enable auto-read while waiting in a waitClick()
        mainContext.getSkipState().setSkipMode(SkipMode.AUTO_READ);

        waitForAllThreads();
    }

    @Test
    public void testWaitClick() {
        loadScript("integration/context/wait-click");
        env.update();

        buttonPress(VKey.TEXT_CONTINUE);
        waitForAllThreads();
    }

}
