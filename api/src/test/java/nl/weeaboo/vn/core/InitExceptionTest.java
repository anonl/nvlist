package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class InitExceptionTest {

    private static final String MESSAGE = "message";
    private static final Exception CAUSE = new RuntimeException("test");

    @Test
    public void test() {
        InitException alpha = new InitException(MESSAGE);
        Assert.assertEquals(MESSAGE, alpha.getMessage());
        Assert.assertNull(alpha.getCause());

        InitException beta = new InitException(CAUSE);
        Assert.assertTrue(beta.getMessage().length() > 0); // Has a non-empty default message
        Assert.assertEquals(CAUSE, beta.getCause());

        InitException gamma = new InitException(MESSAGE, CAUSE);
        Assert.assertEquals(MESSAGE, gamma.getMessage());
        Assert.assertEquals(CAUSE, gamma.getCause());
    }

}
