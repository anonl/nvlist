package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class MediaTypeTest {

    @Test
    public void checkKeycodesUnchanged() {
        int hash = 0;
        for (MediaType e : MediaType.values()) {
            hash ^= e.name().hashCode();
        }
        Assert.assertEquals(-1850196044, hash); // Trigger a test failure if the enum changes
    }

}
