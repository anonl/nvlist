package nl.weeaboo.vn.input;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class KeyCodeTest {

    @Test
    public void checkKeycodesUnchanged() {
        /*
         * Check if the hash derived from the available keycodes has changed. This is used to detect
         * unintended incompatibilities due to renamed/removed keycodes.
         */
        Assert.assertEquals(664241046, EnumTester.hashEnum(KeyCode.class));
    }

}
