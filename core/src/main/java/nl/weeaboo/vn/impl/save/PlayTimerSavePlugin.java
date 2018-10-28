package nl.weeaboo.vn.impl.save;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.stats.IPlayTimer;

final class PlayTimerSavePlugin implements IPersistentSavePlugin {

    private static final Logger LOG = LoggerFactory.getLogger(PlayTimerSavePlugin.class);

    private final IPlayTimer playTimer;
    private final IStorage sharedGlobals;

    PlayTimerSavePlugin(IPlayTimer playTimer, IStorage sharedGlobals) {
        this.playTimer = playTimer;
        this.sharedGlobals = sharedGlobals;
    }

    @Override
    public void loadPersistent(SecureFileWriter writer) {
        try {
            playTimer.load(sharedGlobals);
        } catch (IOException e) {
            LOG.error("Unable to load play timer state from shared globals", e);
        }
    }

    @Override
    public void savePersistent(SecureFileWriter writer) {
        try {
            playTimer.save(sharedGlobals);
        } catch (IOException e) {
            LOG.error("Unable to save play timer state to shared globals", e);
        }
    }

}
