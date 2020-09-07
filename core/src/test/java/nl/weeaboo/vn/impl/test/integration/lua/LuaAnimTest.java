package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Test;

/** Test for anim.lua module */
public class LuaAnimTest extends LuaIntegrationTest {

    @Test
    public void testAnimStartStop() {
        loadScript("integration/anim/startstop");
        waitForAllThreads();
    }

}
