package nl.weeaboo.vn;

import java.io.File;
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

        File saveFolder = new File("save");
        if (!saveFolder.isDirectory() && !saveFolder.mkdirs()) {
            LOG.warn("Unable to create log folder");
        } else {
            InputStream in = Launcher.class.getResourceAsStream("logging.properties");
            if (in == null) {
                LOG.warn("Unable to read logging config");
            } else {
                try {
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
    }

}
