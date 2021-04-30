package nl.weeaboo.vn.desktop.debug;

import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.StackFrame;

/**
 * Test utilities for working with the debug adapter protocol.
 */
final class DapTestHelper {

    static StackFrame stackFrame(String file, int line, String func) {
        StackFrame sf = new StackFrame();
        sf.setSource(source(file));
        sf.setName(func);
        sf.setLine(line);
        return sf;
    }

    static Source source(String file) {
        Source source = new Source();
        source.setName(file);
        source.setPath(NameMapping.toAbsoluteScriptPath(file));
        return source;
    }

    static SourceBreakpoint sourceBreakpoint(int line) {
        SourceBreakpoint sb = new SourceBreakpoint();
        sb.setLine(line);
        return sb;
    }

}
