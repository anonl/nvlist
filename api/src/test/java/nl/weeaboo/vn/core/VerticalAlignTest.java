package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class VerticalAlignTest {

    @Test
    public void checkUnchanged() {
        int hash = 0;
        for (VerticalAlign e : VerticalAlign.values()) {
            hash ^= e.name().hashCode();
        }
        Assert.assertEquals(-223869301, hash); // Trigger a test failure if the enum changes
    }

}
