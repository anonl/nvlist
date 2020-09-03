package nl.weeaboo.vn.desktop.debug;

import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.StackFrame;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

final class Breakpoints {

    private final SetMultimap<String, DebugBreakpoint> breakpointsByAbsolutePath = HashMultimap.create();

    Breakpoint[] setBreakpoints(Source source, SourceBreakpoint[] sourceBreakpoints) {
        String absolutePath = source.getPath();
        if (absolutePath == null) {
            return new Breakpoint[0];
        }

        breakpointsByAbsolutePath.removeAll(absolutePath);
        for (SourceBreakpoint sourceBreakpoint : sourceBreakpoints) {
            breakpointsByAbsolutePath.put(absolutePath, new DebugBreakpoint(absolutePath, sourceBreakpoint));
        }
        return breakpointsByAbsolutePath.get(absolutePath)
                .stream()
                .map(DebugBreakpoint::toDapBreakpoint)
                .toArray(Breakpoint[]::new);
    }

    public boolean shouldPause(StackFrame sf) {
        String relativePath = NameMapping.toRelativeScriptPath(sf.getSource().getPath());
        return shouldPause(relativePath, sf.getLine());
    }

    boolean shouldPause(String relativePath, int line) {
        for (DebugBreakpoint breakpoint : breakpointsByAbsolutePath.values()) {
            if (relativePath.equals(breakpoint.getRelativePath()) && breakpoint.getLineNumber() == line) {
                return true;
            }
        }
        return false;
    }

}
