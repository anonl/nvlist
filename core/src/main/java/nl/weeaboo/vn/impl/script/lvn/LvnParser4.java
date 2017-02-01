package nl.weeaboo.vn.impl.script.lvn;

import static nl.weeaboo.lua2.LuaUtil.escape;
import static nl.weeaboo.lua2.LuaUtil.unescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.script.lvn.TextParser.Token;

class LvnParser4 implements ILvnParser {

    private final TextParser textParser;

    private FilePath filename;
    private ImmutableList<String> sourceLines;
    private List<LvnLine> compiledLines;

    public LvnParser4() {
        textParser = new TextParser();
    }

    private void init(FilePath filename, InputStream in) throws IOException {
        List<String> lines = ParserUtil.readLinesUtf8(in);

        this.filename = filename;
        this.sourceLines = ImmutableList.copyOf(lines);
        this.compiledLines = Lists.newArrayList();
    }

    private void reset() {
        filename = null;
        sourceLines = null;
        compiledLines = null;
    }

    @Override
    public CompiledLvnFile parseFile(FilePath filename, InputStream in) throws LvnParseException, IOException {
        init(filename, in);

        doParseFile();

        // Return result and clear internal state
        CompiledLvnFile result = new CompiledLvnFile(filename, compiledLines);
        reset();
        return result;
    }

    private void doParseFile() throws LvnParseException {
        LvnMode mode = LvnMode.TEXT;
        StringBuilder temp = new StringBuilder();

        int lineNum = 1;
        int textLineNum = 1;
        for (String srcLine : sourceLines) {
            srcLine = srcLine.trim();

            //End single-line modes
            if (mode.isSingleLine()) {
                mode = LvnMode.TEXT;
            }

            //Look for comment starts
            if (mode == LvnMode.TEXT && srcLine.startsWith("#")) {
                mode = LvnMode.COMMENT;
            }
            if (mode == LvnMode.TEXT && srcLine.startsWith("@")) {
                mode = LvnMode.CODE;
            }

            //Process line
            String compiledLine;
            final LvnMode compiledLineMode;
            if (mode == LvnMode.TEXT) {
                compiledLineMode = mode;

                if (srcLine.length() > 0) {
                    parseTextLine(temp, filename, srcLine, textLineNum);
                    textLineNum++;
                    compiledLine = temp.toString();
                    temp.delete(0, temp.length());
                } else {
                    compiledLine = "";
                }
            } else if (mode == LvnMode.CODE || mode == LvnMode.MULTILINE_CODE) {
                if (srcLine.startsWith("@@")) {
                    compiledLineMode = LvnMode.MULTILINE_CODE;

                    compiledLine = srcLine.substring(2);
                    mode = (mode == LvnMode.MULTILINE_CODE ? LvnMode.TEXT : LvnMode.MULTILINE_CODE);
                } else {
                    compiledLineMode = mode;

                    if (mode == LvnMode.CODE && srcLine.startsWith("@")) {
                        compiledLine = srcLine.substring(1);
                    } else {
                        compiledLine = srcLine;
                    }
                }

                if (mode == LvnMode.CODE || mode == LvnMode.MULTILINE_CODE) {
                    compiledLine = parseCodeLine(compiledLine);
                } else {
                    compiledLine = "";
                }
            } else if (mode == LvnMode.COMMENT || mode == LvnMode.MULTILINE_COMMENT) {
                if (srcLine.startsWith("##")) {
                    compiledLineMode = LvnMode.MULTILINE_COMMENT;

                    mode = (mode == LvnMode.MULTILINE_COMMENT ? LvnMode.TEXT : LvnMode.MULTILINE_COMMENT);
                } else {
                    compiledLineMode = mode;
                }

                //Ignore commented lines
                compiledLine = "";
            } else {
                throw new LvnParseException(filename, lineNum, "Invalid mode: " + mode);
            }

            compiledLines.add(new LvnLine(srcLine, compiledLine, compiledLineMode));
            lineNum++;
        }
    }

    protected void parseTextLine(StringBuilder out, FilePath filename, String line, int textLineNum) {
        if (line.length() == 0) {
            return; //Empty line
        }

        line = unescape(line);

        List<String> triggers = new ArrayList<>(4);
        for (Token token : textParser.tokenize(line)) {
            if (token.getType() == TextParser.ETokenType.COMMAND) {
                triggers.add(token.getText());
            }
        }

        out.append("text(\"").append(escape(line)).append("\"");

        if (triggers.isEmpty()) {
            out.append(", nil");
        } else {
            out.append(", {");
            for (String trigger : triggers) {
                out.append("function() ").append(trigger).append(" end, ");
            }
            out.append("}");
        }
        out.append(", {");
        out.append("filename=\"").append(escape(filename.toString())).append("\", ");
        out.append("line=").append(textLineNum).append(",");
        out.append("}");

        out.append(")");
    }

    protected String parseCodeLine(String line) {
        return line.trim();
    }

}
