package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class BlendModeTest {

    @Test
    public void checkKeycodesUnchanged() {
        int hash = 0;
        for (BlendMode e : BlendMode.values()) {
            hash ^= e.name().hashCode();
        }
        Assert.assertEquals(227239553, hash); // Trigger a test failure if the enum changes
    }

}
