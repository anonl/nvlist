package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class MediaTypeTest {

    @Test
    public void checkKeycodesUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(-653089874, EnumTester.hashEnum(MediaType.class));
    }

}
