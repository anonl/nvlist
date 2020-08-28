package nl.weeaboo.vn.langserver;

import java.util.List;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CheckReturnValue;

abstract class SourceMap {

    private final String uri;

    SourceMap(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public String getShortName() {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    /**
     * @param lineOffset Zero-based
     */
    protected abstract Line lineAt(int lineOffset);

    protected abstract List<? extends Line> getLines();

    @CheckReturnValue
    static Range mergeRanges(Range first, Range... others) {
        Position startPos = first.getStart();
        Position endPos = first.getEnd();
        for (Range other : others) {
            startPos = min(startPos, other.getStart());
            endPos = max(endPos, other.getEnd());
        }
        return new Range(startPos, endPos);
    }

    private static Position min(Position a, Position b) {
        if (a.getLine() < b.getLine()) {
            return a;
        } else if (a.getLine() > b.getLine()) {
            return b;
        }
        return new Position(a.getLine(), Math.min(a.getCharacter(), b.getCharacter()));
    }

    private static Position max(Position a, Position b) {
        if (a.getLine() > b.getLine()) {
            return a;
        } else if (a.getLine() < b.getLine()) {
            return b;
        }
        return new Position(a.getLine(), Math.max(a.getCharacter(), b.getCharacter()));
    }

    protected final Range range(ParserRuleContext ctx) {
        int start = ctx.start.getStartIndex();
        int stop = ctx.stop.getStopIndex() + 1;

        return new Range(fileOffsetToPos(start), fileOffsetToPos(stop));
    }

    protected final Position fileOffsetToPos(int charOffsetInFile) {
        List<? extends Line> lines = getLines();
        Line line = lineAtCharOffsetInFile(lines, charOffsetInFile);
        if (line == null) {
            return new Position(lines.size() + 1, 0);
        }
        return new Position(line.lineIndex, charOffsetInFile - line.charOffsetInFile);
    }

    /**
     * Returns the word the given position.
     *
     * @param pos Text position.
     */
    protected String getWordAt(Position pos) {
        Line line = lineAt(pos.getLine());
        if (line == null) {
            return "";
        }
        return line.getWordAt(pos.getCharacter());
    }

    /**
     * Returns the location of the definition of the element at the given position.
     *
     * @param pos Text position.
     */
    protected @Nullable Range getDefinitionAt(Position pos) {
        return null;
    }

    /**
     * Returns the function with the given name, or {@code null} if not found.
     *
     * @param name The name of the function.
     */
    protected @Nullable Function getFunction(String name) {
        return null;
    }

    @Nullable
    protected static <L extends Line> L lineAtCharOffsetInFile(List<? extends L> lines, int n) {
        for (L line : lines) {
            int relOffset = n - line.charOffsetInFile;
            if (relOffset >= 0 && relOffset < line.contents.length()) {
                return line;
            }
        }
        return null;
    }

    protected static class Line {

        /** Zero-based */
        final int lineIndex;
        final int charOffsetInFile;
        final String contents;

        Line(int lineIndex, int charOffsetInFile, String contents) {
            this.lineIndex = lineIndex;
            this.charOffsetInFile = charOffsetInFile;
            this.contents = contents;
        }

        protected boolean isWordChar(char c) {
            return Character.isLetterOrDigit(c);
        }

        public String getWordAt(int charPos) {
            int start = charPos;
            while (start > 0 && isWordChar(contents.charAt(start - 1))) {
                start--;
            }

            int end = charPos;
            while (end < contents.length() && isWordChar(contents.charAt(end))) {
                end++;
            }

            return contents.substring(start, end);
        }

    }

    protected static final class Function {

        final String name;
        final Range headerRange;
        final Range bodyRange;
        final List<String> paramNames = Lists.newArrayList();

        String headerComment = "";

        Function(String name, Range headerRange, Range bodyRange) {
            this.name = name;
            this.headerRange = headerRange;
            this.bodyRange = bodyRange;
        }

    }

}
