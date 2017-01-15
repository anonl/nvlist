package nl.weeaboo.vn.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IResourceResolver;

public interface IScriptLoader extends IResourceResolver {

    /**
     * Opens the given script file as an inputstream.
     *
     * @throws FileNotFoundException If the file could not be openend.
     * @throws IOException If an exception occurs while trying to open the script.
     */
    public InputStream openScript(FilePath filename) throws FileNotFoundException, IOException;

    /**
     * Executes a script file.
     *
     * @param thread The thread to execute the script on.
     * @throws IOException If an exception occurs while trying to open the script.
     * @throws ScriptException If an exception occurs while executing the script.
     */
    public void loadScript(IScriptThread thread, FilePath filename) throws IOException, ScriptException;

    /**
     * Returns the paths for all script files in the specified folder and its sub-folders.
     */
    public Collection<FilePath> getScriptFiles(FilePath folder);

}
