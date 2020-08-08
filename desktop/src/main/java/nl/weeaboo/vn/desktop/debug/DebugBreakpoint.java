package nl.weeaboo.vn.desktop.debug;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DebugBreakpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DebugBreakpoint.class);

    private final String absolutePath;
    private final String relativePath;
    private final int lineNumber;

    DebugBreakpoint(String absolutePath, SourceBreakpoint bp) {
        this(absolutePath, bp.getLine());
    }

    DebugBreakpoint(String absolutePath, int lineNumber) {
        this.absolutePath = Objects.requireNonNull(absolutePath);
        this.lineNumber = lineNumber;

        this.relativePath = relativize(absolutePath);
    }

    private static String relativize(String absolutePath) {
        try {
            URI scriptFolderUri = new File("res/script").getCanonicalFile().toURI();
            URI absoluteUri = new File(absolutePath).getCanonicalFile().toURI();
            return scriptFolderUri.relativize(absoluteUri).getPath();
        } catch (IOException ioe) {
            LOG.warn("Unable to determine relative path for {}", absolutePath, ioe);
            return absolutePath;
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    Breakpoint toDapBreakpoint() {
        Source source = new Source();
        source.setPath(absolutePath);

        Breakpoint result = new Breakpoint();
        result.setSource(source);
        result.setLine(lineNumber);
        return result;
    }
}
