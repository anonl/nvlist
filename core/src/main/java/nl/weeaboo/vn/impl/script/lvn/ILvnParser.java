package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;

import nl.weeaboo.filesystem.FilePath;

/**
 * Parses ".lvn" files.
 */
public interface ILvnParser {

    /**
     * Parses a ".lvn" file.
     *
     * @see #parseFile(FilePath, InputStream)
     */
    ICompiledLvnFile parseFile(FilePath filename, String input) throws LvnParseException, IOException;

    /**
     * Parses a ".lvn" file.
     *
     * @throws LvnParseException If the file contains a syntax error.
     * @throws IOException If an I/O error occurs while trying to read the file contents from the given input stream.
     */
    ICompiledLvnFile parseFile(FilePath filename, InputStream in) throws LvnParseException, IOException;

}
