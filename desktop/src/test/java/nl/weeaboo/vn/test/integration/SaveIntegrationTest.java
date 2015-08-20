package nl.weeaboo.vn.test.integration;

import java.io.IOException;

import org.junit.Test;

import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.SaveParams;

public class SaveIntegrationTest extends IntegrationTest {

    @Test
    public void saveLoad() throws SaveFormatException, IOException {
        ISaveModule saveModule = novel.getEnv().getSaveModule();

        int slot = 1;
        SaveParams saveParams = new SaveParams();
        saveModule.save(novel, slot, saveParams, null);
        saveModule.load(novel, slot, null);
    }

}
