package nl.weeaboo.vn.impl.script.lvn;

import static nl.weeaboo.lua2.LuaUtil.escape;
import static nl.weeaboo.lua2.LuaUtil.unescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import nl.weeaboo.filesystem.FilePath;

class LvnParser3 implements ILvnParser {

    private List<LvnLine> compiledLines;

    public LvnParser3() {
    }

    @Override
    public CompiledLvnFile parseFile(FilePath filename, InputStream in) throws LvnParseException, IOException {
        List<String> src = ParserUtil.readLinesUtf8(in);

        compiledLines = Lists.newArrayList();

        parseLines(filename, src);
        return new CompiledLvnFile(filename, compiledLines);
    }

    private void parseLines(FilePath filename, List<String> lines) throws LvnParseException {
        LvnMode mode = LvnMode.TEXT;

        int lineNum = 1;
        int textLineNum = 1;
        for (String srcLine : lines) {
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
                    compiledLine = parseTextLine(filename, srcLine, textLineNum);
                    textLineNum++;
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

    protected String parseTextLine(FilePath filename, String line, int textLineNum) {
        if (line.length() == 0) {
            return ""; //Empty line
        }

        List<String> out = new ArrayList<>(8);
        out.add(beginParagraphCommand(filename, textLineNum));

        StringBuilder sb = new StringBuilder(line.length());
        for (int n = 0; n < line.length(); n++) {
            char c = line.charAt(n);
            if (c == '\\') {
                n++;
                if (n < line.length()) {
                    c = line.charAt(n);
                    sb.append(unescape(c));
                } else {
                    sb.append('\\');
                }
            } else if (c == '[' || c == '$'/* || c == '{'*/) { //Read [lua code] or $stringify or ${stringify}
                parseTextLine_flush(out, sb);

                //Find block start/end characters
                int start = n + 1;
                char startChar = c;
                char endChar = ' ';
                if (startChar == '[') {
                    endChar = ']';
                //} else if (startChar == '{') {
                //    endChar = '}';
                } else if (startChar == '$' && start < line.length() && line.charAt(start) == '{') {
                    start++;
                    endChar = '}';
                }

                //Find block end
                int end = ParserUtil.findBlockEnd(line, start, endChar);

                //Process block
                if (end > start) {
                    String str = line.substring(start, end);
                    if (startChar == '$') {
                        out.add(parseStringifier(str));
                    //} else if (startChar == '{') {
                    //    out.add(parseTextTag(str));
                    } else {
                        out.add(parseCodeLine(str));
                    }
                }

                n = end;
            } else {
                sb.append(c);
            }
        }
        parseTextLine_flush(out, sb);
        out.add(endParagraphCommand());

        //Merge out lines into a String
        for (String outLine : out) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(outLine);
        }
        return sb.toString();
    }

    protected void parseTextLine_flush(Collection<String> out, StringBuilder sb) {
        if (sb.length() > 0) { //Flush buffered chars
            String ln = appendTextCommand(sb.toString());
            if (ln.length() > 0) {
                out.add(ln);
            }
            sb.delete(0, sb.length());
        }
    }

    protected String parseCodeLine(String line) {
        return line.trim();
    }

    protected String parseStringifier(String str) {
        return "paragraph.stringify(\"" + escape(str) + "\")";
    }

    protected String beginParagraphCommand(FilePath filename, int textLineNum) {
        return "paragraph.start(\"" + escape(filename.toString()) + "\", " + textLineNum + ")";
    }

    protected String appendTextCommand(String line) {
        if (line.length() == 0) {
            return "";
        }

        line = ParserUtil.collapseWhitespace(escape(line), false);

        if (line.length() == 0) {
            return "";
        }
        return "paragraph.append(\"" + line + "\")";
    }

    protected String endParagraphCommand() {
        return "paragraph.finish()";
    }

}
