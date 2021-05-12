package nl.weeaboo.vn.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IResourceResolver;

/**
 * Loads script resources.
 */
public interface IScriptLoader extends IResourceResolver {

    /**
     * Opens the given script file as an inputstream.
     *
     * @throws FileNotFoundException If the file could not be openend.
     * @throws IOException If an exception occurs while trying to open the script.
     */
    public InputStream openScript(FilePath filename) throws FileNotFoundException, IOException;

    /**
     * Returns the paths for all script files in the specified folder and its sub-folders.
     */
    public Collection<FilePath> getScriptFiles(FilePath folder);

}
