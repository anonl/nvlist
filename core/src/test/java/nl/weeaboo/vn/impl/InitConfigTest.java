package nl.weeaboo.vn.impl;

import java.lang.Thread.UncaughtExceptionHandler;

import org.junit.Assert;
import org.junit.Test;

public class InitConfigTest {

    @Test
    public void test() {
        UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        InitConfig.init();

        // Exception handler is changed
        UncaughtExceptionHandler newExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Assert.assertNotSame(oldExceptionHandler, newExceptionHandler);
        newExceptionHandler.uncaughtException(Thread.currentThread(), new RuntimeException("test"));
    }

}
