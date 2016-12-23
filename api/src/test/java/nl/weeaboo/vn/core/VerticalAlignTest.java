package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class VerticalAlignTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(-361260565, EnumTester.hashEnum(VerticalAlign.class));
    }

}
