package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.vn.impl.script.lvn.TextParser.Token;

final class LvnParser4 extends AbstractLvnParser {

    private final TextParser textParser;

    private Input input;
    private Output output;

    public LvnParser4() {
        textParser = new TextParser();
        reset();
    }

    private void init(FilePath filename, InputStream in) throws IOException {
        List<String> lines = ParserUtil.readLinesUtf8(in);

        this.input = new Input(filename, lines);
        this.output = new Output();
    }

    private void reset() {
        this.input = new Input(FilePath.empty(), ImmutableList.of());
        this.output = new Output();
    }

    @Override
    public CompiledLvnFile parseFile(FilePath filename, InputStream in) throws IOException {
        init(filename, in);

        doParseFile();

        // Return result and clear internal state
        CompiledLvnFile result = new CompiledLvnFile(filename, output.lines);
        reset();
        return result;
    }

    private void doParseFile() {
        while (input.hasRemainingLines()) {
            String line = input.readLine();
            if (line.startsWith("##")) {
                output.addEmpty(LvnMode.MULTILINE_COMMENT, line);
                parseCommentBlock();
            } else if (line.startsWith("#")) {
                output.addEmpty(LvnMode.COMMENT, line);
            } else if (line.startsWith("@@")) {
                output.addEmpty(LvnMode.MULTILINE_CODE, line);
                parseCodeBlock();
            } else if (line.startsWith("@")) {
                output.addLine(LvnMode.CODE, line, line.substring(1));
            } else {
                parseTextLine(line);
            }
        }
    }

    private void parseCodeBlock() {
        while (input.hasRemainingLines()) {
            String line = input.readLine();
            if (line.startsWith("@@")) {
                output.addEmpty(LvnMode.MULTILINE_CODE, line);
                break;
            }
            output.addLine(LvnMode.MULTILINE_CODE, line, line);
        }
    }

    private void parseCommentBlock() {
        while (input.hasRemainingLines()) {
            String line = input.readLine();
            if (line.startsWith("##")) {
                output.addEmpty(LvnMode.MULTILINE_COMMENT, line);
                break;
            }
            output.addEmpty(LvnMode.MULTILINE_COMMENT, line);
        }
    }

    private void parseTextLine(String line) {
        if (line.length() == 0) {
            output.addEmpty(LvnMode.TEXT, line);
            return; // Empty line
        }

        final int lineNumber = ++output.textLineCount; // 1-based

        List<String> triggers = new ArrayList<>(4);
        for (Token token : textParser.tokenize(line)) {
            if (token.getType() == TextParser.ETokenType.COMMAND) {
                triggers.add(token.getText());
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("text(\"").append(escapeText(line)).append("\"");
        if (triggers.isEmpty()) {
            out.append(", nil");
        } else {
            out.append(", {");
            for (String trigger : triggers) {
                out.append("function() ").append(trigger).append(" end, ");
            }
            out.append("}");
        }
        out.append(", {filename=\"");
        out.append(LuaUtil.escape(input.filename.toString()));
        out.append("\", line=");
        out.append(lineNumber);
        out.append(",})");
        output.addLine(LvnMode.TEXT, line, out.toString());
    }

    /**
     * Escapes an LVN text line for use in a Lua String literal.
     * <p>
     * Conveniently, LVN and Lua strings use almost the same escape sequences.
     */
    private static String escapeText(String str) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < str.length(); n++) {
            char c = str.charAt(n);
            if (c == '\\') {
                sb.append('\\');
                n++;

                if (n < str.length()) {
                    c = str.charAt(n);

                    /*
                     * Some characters are only special to NVList, we need to double-escape those since Lua
                     * will unescape the contents of the string during parsing.
                     */
                    switch (c) {
                    case '$':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        sb.append('\\');
                    }
                    sb.append(c);
                }
            } else if (c == '"') {
                // Escape double quotes
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static final class Input {

        private final FilePath filename;
        private final ImmutableList<String> sourceLines;
        private int currentLine;

        public Input(FilePath filename, List<String> lines) {
            this.filename = filename;
            this.sourceLines = ImmutableList.copyOf(lines);
        }

        public boolean hasRemainingLines() {
            return currentLine < sourceLines.size();
        }

        public String readLine() {
            return sourceLines.get(currentLine++).trim();
        }

    }

    private static final class Output {

        private final List<LvnLine> lines = new ArrayList<>();
        private int textLineCount;

        private void addEmpty(LvnMode mode, String input) {
            addLine(mode, input, "");
        }

        private void addLine(LvnMode mode, String input, String output) {
            lines.add(new LvnLine(input, output, mode));
        }

    }
}
