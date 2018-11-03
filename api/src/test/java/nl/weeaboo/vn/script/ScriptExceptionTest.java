package nl.weeaboo.vn.script;

import org.junit.Assert;
import org.junit.Test;

public final class ScriptExceptionTest {

    @Test
    public void testConstructors() {
        ScriptException ex = new ScriptException("x");
        Assert.assertEquals("x", ex.getMessage());
        Assert.assertEquals(null, ex.getCause());

        RuntimeException cause = new RuntimeException();
        ex = new ScriptException("y", cause);
        Assert.assertEquals("y", ex.getMessage());
        Assert.assertEquals(cause, ex.getCause());
    }

}
