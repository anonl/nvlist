package nl.weeaboo.vn.sound;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class SoundTypeTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(-693890706, EnumTester.hashEnum(SoundType.class));
    }

}
