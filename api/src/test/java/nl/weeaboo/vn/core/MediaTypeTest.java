package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class MediaTypeTest {

    @Test
    public void checkEnumValuesChanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(562575701, EnumTester.hashEnum(MediaType.class));
    }

}
