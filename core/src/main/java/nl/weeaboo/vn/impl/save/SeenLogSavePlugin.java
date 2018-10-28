package nl.weeaboo.vn.impl.save;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.stats.ISeenLogHolder;

final class SeenLogSavePlugin implements IPersistentSavePlugin {

    private static final Logger LOG = LoggerFactory.getLogger(SeenLogSavePlugin.class);
    private static final FilePath SEEN_LOG_PATH = FilePath.of("seen.bin");

    private final ISeenLogHolder seenLog;

    SeenLogSavePlugin(ISeenLogHolder seenLog) {
        this.seenLog = seenLog;
    }

    @Override
    public void loadPersistent(SecureFileWriter writer) {
        try {
            seenLog.load(writer, SEEN_LOG_PATH);
        } catch (FileNotFoundException fnfe) {
            // Seen log doesn't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading seen log", ioe);
        }
    }

    @Override
    public void savePersistent(SecureFileWriter writer) {
        try {
            seenLog.save(writer, SEEN_LOG_PATH);
        } catch (IOException e) {
            LOG.error("Unable to save seen log", e);
        }
    }

}
