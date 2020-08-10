package nl.weeaboo.vn.desktop.debug;

import java.util.Objects;

import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;

final class DebugBreakpoint {

    private final String absolutePath;
    private final String relativePath;
    private final int lineNumber;

    DebugBreakpoint(String absolutePath, SourceBreakpoint bp) {
        this(absolutePath, bp.getLine());
    }

    DebugBreakpoint(String absolutePath, int lineNumber) {
        this.absolutePath = Objects.requireNonNull(absolutePath);
        this.lineNumber = lineNumber;

        this.relativePath = NameMapping.toRelativeScriptPath(absolutePath);
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
        result.setVerified(true);
        return result;
    }
}
