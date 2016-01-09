package nl.weeaboo.vn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class InitConfig {

    private static final Logger LOG = LoggerFactory.getLogger(InitConfig.class);

    private InitConfig() {
    }

    public static void init() {
        configLogging();
    }

    private static void configLogging() {
        System.setProperty("sun.io.serialization.extendedDebugInfo", "true");

        try {
            InputStream in = Launcher.class.getResourceAsStream("logging.properties");
            if (in == null) {
                throw new FileNotFoundException();
            }
            try {
                LogManager.getLogManager().readConfiguration(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            LOG.warn("Unable to read logging config", e);
        }
    }

}
