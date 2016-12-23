package nl.weeaboo.vn.layout;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class LayoutSizeTypeTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(-1852556171, EnumTester.hashEnum(LayoutSizeType.class));
    }

}
