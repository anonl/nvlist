package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class BlendModeTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(-935107359, EnumTester.hashEnum(BlendMode.class));
    }

}
