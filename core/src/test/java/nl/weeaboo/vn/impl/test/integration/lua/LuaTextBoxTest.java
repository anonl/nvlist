package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

public class LuaTextBoxTest extends LuaIntegrationTest {

    @Test
    public void testClickIndicator() {
        loadScript("integration/textbox/click-indicator");
        waitForAllThreads();
    }

    @Test
    public void testTextOnOff() {
        loadScript("integration/textbox/textonoff");
        waitForAllThreads();
    }

}
