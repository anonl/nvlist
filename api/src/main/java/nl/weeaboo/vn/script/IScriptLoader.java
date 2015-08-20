package nl.weeaboo.vn.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;

public interface IScriptLoader extends Serializable {

	/**
	 * @param pattern The filename, or filename pattern.
	 * @return The canonical filename of a script file, or {@code null} if the given pattern did not match any
	 *         existing script file.
	 */
	public String findScriptFile(String pattern);

	/**
	 * Opens the given script file as an inputstream.
	 *
	 * @param normalizedFilename The canonical filename of the script file to open (i.e. the result of a call
	 *        to {@link #findScriptFile(String)}).
	 * @throws FileNotFoundException If the file could not be openend.
	 * @throws IOException If an exception occurs while trying to open the script.
	 * @see #findScriptFile(String)
	 */
	public InputStream openScript(String normalizedFilename) throws FileNotFoundException, IOException;

	/**
     * Executes a script file.
     *
     * @param thread The thread to execute the script on.
     * @param normalizedFilename The canonical filename of the script file to open (i.e. the result of a call
     *        to {@link #findScriptFile(String)}).
     * @throws IOException If an exception occurs while trying to open the script.
     * @throws ScriptException If an exception occurs while executing the script.
     * @see #findScriptFile(String)
	 */
	public void loadScript(IScriptThread thread, String normalizedFilename) throws IOException, ScriptException;

    /**
     * Returns the paths for all script files in the specified folder and its sub-folders.
     */
    public Collection<String> getScriptFiles(String folder);

}
