package nl.weeaboo.vn.test.integration;

import org.junit.Test;

public class SaveIntegrationTest extends IntegrationTest {

    @Test
    public void saveLoad() {
        loadScript("integration/save/saveload.lvn");

        waitForAllThreads();
    }

}
