package nl.weeaboo.vn.save;

import org.junit.Assert;
import org.junit.Test;

public final class SaveFormatExceptionTest {

    @Test
    public void testConstructors() {
        SaveFormatException ex = new SaveFormatException("x");
        Assert.assertEquals("x", ex.getMessage());
        Assert.assertEquals(null, ex.getCause());

        RuntimeException cause = new RuntimeException();
        ex = new SaveFormatException("y", cause);
        Assert.assertEquals("y", ex.getMessage());
        Assert.assertEquals(cause, ex.getCause());
    }

}
