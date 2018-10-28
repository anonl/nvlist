package nl.weeaboo.vn.impl.save;

import nl.weeaboo.filesystem.SecureFileWriter;

/**
 * Add-on code to run when loading/saving persistent data using the {@link SaveModule}.
 */
interface IPersistentSavePlugin {

    /**
     * Loads the persistent data. Any errors that occur will be handled inside this method.
     */
    void loadPersistent(SecureFileWriter writer);

    /**
     * Saves the persistent data. Any errors that occur will be handled inside this method.
     */
    void savePersistent(SecureFileWriter writer);

}
