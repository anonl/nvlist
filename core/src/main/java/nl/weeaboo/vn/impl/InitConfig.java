package nl.weeaboo.vn.impl;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs basic initialization of global state.
 */
public final class InitConfig {

    private static final Logger LOG = LoggerFactory.getLogger(InitConfig.class);

    private InitConfig() {
    }

    /** Initializes JVM global configuration (such as logging). */
    public static void init() {
        configLogging();

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error("Uncaught exception from {}", t, e);
            }
        });
    }

    private static void configLogging() {
        System.setProperty("sun.io.serialization.extendedDebugInfo", "true");

        try {
            Class<?> bridgeHandler = Class.forName("org.slf4j.bridge.SLF4JBridgeHandler");
            try {
                bridgeHandler.getMethod("removeHandlersForRootLogger").invoke(null);
                bridgeHandler.getMethod("install").invoke(null);
            } catch (ReflectiveOperationException | SecurityException e) {
                LOG.debug("Unexpected exception while trying to install JUL->SLF4J bridge", e);
            }
        } catch (ClassNotFoundException cnfe) {
            LOG.debug("No JUL->SLF4J bridge found; java.util.logging may not be included in the log output", cnfe);
        }
    }

}
