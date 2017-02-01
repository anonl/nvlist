package nl.weeaboo.vn.impl.input;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.impl.input.InputConfig;
import nl.weeaboo.vn.impl.save.JsonUtil;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.VKey;

public class InputConfigTest {

    private static final Logger LOG = LoggerFactory.getLogger(InputConfigTest.class);

    private InputConfig inputConfig;

    @Before
    public void before() {
        inputConfig = new InputConfig();
        inputConfig.add(VKey.UP, KeyCode.UP);
        inputConfig.add(VKey.DOWN, KeyCode.DOWN);
        inputConfig.add(VKey.LEFT, KeyCode.LEFT);
        inputConfig.add(VKey.RIGHT, KeyCode.RIGHT);
    }

    /** Test serialization of input config */
    @Test
    public void saveLoad() {
        String json = JsonUtil.toJson(inputConfig);
        LOG.info("JSON: {}", json);

        InputConfig deserialized = JsonUtil.fromJson(InputConfig.class, json);

        for (VKey vkey : VKey.getStandardKeys()) {
            Assert.assertEquals(inputConfig.get(vkey), deserialized.get(vkey));
        }
    }

    /** Read default input config */
    @Test
    public void defaultConfig() throws IOException {
        InputConfig config = InputConfig.readDefaultConfig();

        // All standard keys must be mapped
        for (VKey vkey : VKey.getStandardKeys()) {
            Collection<KeyCode> mapped = config.get(vkey);
            LOG.info("Default {}: {}", vkey, mapped);
            Assert.assertNotEquals(0, mapped.size());
        }
    }
}
