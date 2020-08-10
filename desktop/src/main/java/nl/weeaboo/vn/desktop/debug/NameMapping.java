package nl.weeaboo.vn.desktop.debug;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NameMapping {

    private static final Logger LOG = LoggerFactory.getLogger(DebugBreakpoint.class);
    private static final File SCRIPT_FOLDER = new File("res/script");

    static String toRelativeScriptPath(String absolutePath) {
        if (absolutePath.startsWith("builtin")) {
            return absolutePath;
        }

        try {
            URI scriptFolderUri = SCRIPT_FOLDER.getCanonicalFile().toURI();
            URI absoluteUri = new File(absolutePath).getCanonicalFile().toURI();
            return scriptFolderUri.relativize(absoluteUri).getPath();
        } catch (IOException ioe) {
            LOG.warn("Unable to determine relative path for {}", absolutePath, ioe);
            return absolutePath;
        }
    }

    static String toAbsoluteScriptPath(String relativePath) {
        if (relativePath.startsWith("builtin")) {
            return relativePath;
        }

        try {
            return new File(SCRIPT_FOLDER, relativePath).getCanonicalPath();
        } catch (IOException ioe) {
            LOG.warn("Unable to determine absolute path for {}", relativePath, ioe);
            return relativePath;
        }
    }

}
