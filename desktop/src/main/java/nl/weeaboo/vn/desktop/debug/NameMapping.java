package nl.weeaboo.vn.desktop.debug;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Performs file path conversions.
 */
final class NameMapping {

    private static final Path SCRIPT_FOLDER = Paths.get("res/script").toAbsolutePath();

    /**
     * Turns an absolute path to a file in the res/script folder into a relative path.
     *
     * @see #toAbsoluteScriptPath(String)
     */
    static String toRelativeScriptPath(String absolutePath) {
        if (absolutePath.startsWith("builtin")) {
            return absolutePath;
        }

        URI scriptFolderUri = SCRIPT_FOLDER.toUri();
        URI uri = Paths.get(absolutePath).toAbsolutePath().toUri();
        if (uri.getPath().startsWith(scriptFolderUri.getPath())) {
            return scriptFolderUri.relativize(uri).getPath();
        } else {
            // Path isn't relative to the script folder
            return absolutePath;
        }
    }

    /**
     * Turns a relative path to a file in the res/script folder into an absolute path.
     *
     * @see #toRelativeScriptPath(String)
     */
    static String toAbsoluteScriptPath(String relativePath) {
        if (relativePath.startsWith("builtin")) {
            return relativePath;
        }

        return SCRIPT_FOLDER.resolve(relativePath).toAbsolutePath().toString();
    }

}
