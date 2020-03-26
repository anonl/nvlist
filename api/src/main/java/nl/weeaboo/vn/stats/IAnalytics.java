package nl.weeaboo.vn.stats;

import java.io.IOException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.core.IUpdateable;

/**
 * Logs interesting events for later analysis.
 */
public interface IAnalytics extends IUpdateable, IResourceLoadLog {

    /**
     * Loads analytics log from a file.
     *
     * @throws IOException If an I/O error occurs while trying to read the file.
     * @see #save(SecureFileWriter, FilePath)
     */
    void load(SecureFileWriter sfw, FilePath path) throws IOException;

    /**
     * Stores analytics to a file.
     *
     * @throws IOException If an I/O error occurs while trying to write the file.
     * @see #load(SecureFileWriter, FilePath)
     */
    void save(SecureFileWriter sfw, FilePath path) throws IOException;

}
