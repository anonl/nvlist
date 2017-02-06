package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;

public interface ISeenLogHolder extends Serializable {

    /** Returns the log for tracking which resource files were 'seen' by the user */
    IResourceSeenLog getResourceLog();

    /** Returns the log for tracking which choice options have been selected by the user */
    IChoiceSeenLog getChoiceLog();

    /** Returns the log for tracking which text lines were read by the user */
    IScriptSeenLog getScriptLog();

    /**
     * Loads a seen log from a file.
     *
     * @throws IOException If an I/O error occurs while trying to read the file.
     * @see #save(SecureFileWriter, FilePath)
     */
    void load(SecureFileWriter sfw, FilePath path) throws IOException;

    /**
     * Stores the seen logs in a file.
     *
     * @throws IOException If an I/O error occurs while trying to write the file.
     * @see #load(SecureFileWriter, FilePath)
     */
    void save(SecureFileWriter sfw, FilePath path) throws IOException;

}
