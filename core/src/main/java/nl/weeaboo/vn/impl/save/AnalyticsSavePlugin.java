package nl.weeaboo.vn.impl.save;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.stats.IAnalytics;

final class AnalyticsSavePlugin implements IPersistentSavePlugin {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsSavePlugin.class);

    // Note: This path is also used in common-desktop.gradle
    private static final FilePath ANALYTICS_PATH = FilePath.of("analytics.bin");

    private final IAnalytics analytics;

    AnalyticsSavePlugin(IAnalytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public void loadPersistent(SecureFileWriter writer) {
        try {
            analytics.load(writer, ANALYTICS_PATH);
        } catch (FileNotFoundException fnfe) {
            // Analytics file doesn't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading analytics", ioe);
        }
    }

    @Override
    public void savePersistent(SecureFileWriter writer) {
        try {
            analytics.save(writer, ANALYTICS_PATH);
        } catch (IOException e) {
            LOG.error("Unable to save analytics", e);
        }
    }

}
