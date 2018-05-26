package nl.weeaboo.vn.impl;

public final class InitConfig {

    private InitConfig() {
    }

    /** Initializes JVM global configuration (such as logging). */
    public static void init() {
        configLogging();
    }

    private static void configLogging() {
        System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
    }

}
