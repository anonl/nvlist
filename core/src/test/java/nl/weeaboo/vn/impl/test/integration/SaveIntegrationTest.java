package nl.weeaboo.vn.impl.test.integration;

import org.junit.Test;

public class SaveIntegrationTest extends IntegrationTest {

    @Test
    public void saveLoad() {
        loadScript("integration/save/saveload.lvn");

        waitForAllThreads();
    }

    /**
     * Create/delete multiple save files
     */
    @Test
    public void manageSaves() {
        loadScript("integration/save/managesaves.lvn");

        waitForAllThreads();
    }

}
