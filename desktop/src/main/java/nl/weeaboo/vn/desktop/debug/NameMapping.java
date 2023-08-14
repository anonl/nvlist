package nl.weeaboo.vn.desktop.debug;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

/**
 * Performs file path conversions.
 */
final class NameMapping {

    private static final Logger LOG = LoggerFactory.getLogger(NameMapping.class);

    @VisibleForTesting
    static Path scriptFolder = Paths.get("res/script").toAbsolutePath();

    /**
     * Turns an absolute path to a file in the res/script folder into a relative path.
     *
     * @see #toAbsoluteScriptPath(String)
     */
    static String toRelativeScriptPath(String absolutePath) {
        if (absolutePath.startsWith("builtin")) {
            return absolutePath;
        }

        try {
            Path basePath = scriptFolder.toRealPath();
            Path filePath = Paths.get(absolutePath).toRealPath();

            return basePath.relativize(filePath).toString();
        } catch (NoSuchFileException e) {
            LOG.warn("Script folder doesn't exist: {}", scriptFolder);
        } catch (IOException e) {
            // Path isn't relative to the script folder
            LOG.debug("Name mapping called with non-relative path: {}", absolutePath, e);
        }
        return absolutePath;
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

        return scriptFolder.resolve(relativePath).toAbsolutePath().toString();
    }

}
