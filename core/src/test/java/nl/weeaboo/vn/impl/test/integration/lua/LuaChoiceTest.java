package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

public class LuaChoiceTest extends LuaIntegrationTest {

    @Test
    public void testBasicChoice() {
        loadScript("integration/choice/basic-choice");
        waitForAllThreads();
    }

}
