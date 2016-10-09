package nl.weeaboo.vn.input;

import org.junit.Assert;
import org.junit.Test;

public class KeyCodeTest {

    @Test
    public void checkKeycodesUnchanged() {
        int hash = 0;
        for (KeyCode key : KeyCode.values()) {
            hash ^= key.name().hashCode();
        }

        /*
         * Check if the hash derived from the available keycodes has changed. This is used to detect
         * unintended incompatibilities due to renamed/removed keycodes.
         */
        Assert.assertEquals(456253704, hash);
    }

}
