package nl.weeaboo.vn.impl.save;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.save.IStorage;

final class SharedGlobalsSavePlugin implements IPersistentSavePlugin {

    private static final Logger LOG = LoggerFactory.getLogger(SharedGlobalsSavePlugin.class);

    private static final FilePath SHARED_GLOBALS_PATH = FilePath.of("save-shared.bin");

    private final IStorage sharedGlobals;

    SharedGlobalsSavePlugin(IStorage sharedGlobals) {
        this.sharedGlobals = sharedGlobals;
    }

    @Override
    public void loadPersistent(SecureFileWriter writer) {
        try {
            // TODO LVN-017
            // if (isVNDS()) {
            //    sharedGlobals.set(VndsUtil.readVndsGlobalSav(fs));
            // }
            IStorage read = StorageIO.read(writer, SHARED_GLOBALS_PATH);

            sharedGlobals.clear();
            sharedGlobals.addAll(read);
        } catch (FileNotFoundException fnfe) {
            // Shared globals don't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading shared globals", ioe);
        }
    }

    @Override
    public void savePersistent(SecureFileWriter writer) {
        try {
            StorageIO.write(sharedGlobals, writer, SHARED_GLOBALS_PATH);
        } catch (IOException e) {
            LOG.error("Unable to save shared globals", e);
        }
    }

}
