package nl.weeaboo.vn.script.lvn;

import static nl.weeaboo.lua2.LuaUtil.escape;
import static nl.weeaboo.lua2.LuaUtil.unescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class LvnParser3 implements ILvnParser {

	private String[] compiledLines;
	private LvnMode[] compiledModes;

	public LvnParser3() {
	}

	//Functions
	@Override
	public CompiledLvnFile parseFile(String filename, InputStream in) throws LvnParseException, IOException {
		String[] src = ParserUtil.readLinesUtf8(in);
		
		parseLines(filename, src, 1, 1);
		return new CompiledLvnFile(filename, src, compiledLines, compiledModes);
	}

	private void parseLines(String filename, String[] lines, int startLineNum,
			int textLineNum) throws LvnParseException
	{
		compiledLines = new String[lines.length];
		compiledModes = new LvnMode[lines.length];

		LvnMode mode = LvnMode.TEXT;
		for (int n = 0; n < lines.length; n++) {
			String line = lines[n].trim();

			//End single-line modes
			if (mode.isSingleLine()) mode = LvnMode.TEXT;

			//Look for comment starts
			if (mode == LvnMode.TEXT && line.startsWith("#")) mode = LvnMode.COMMENT;
			if (mode == LvnMode.TEXT && line.startsWith("@")) mode = LvnMode.CODE;

			//Process line
			if (mode == LvnMode.TEXT) {
				compiledModes[n] = mode;
				if (line.length() > 0) {
					line = parseTextLine(filename, line, textLineNum);
					textLineNum++;
				}
			} else if (mode == LvnMode.CODE || mode == LvnMode.MULTILINE_CODE) {
				if (line.startsWith("@@")) {
					compiledModes[n] = LvnMode.MULTILINE_CODE;

					line = line.substring(2);
					mode = (mode == LvnMode.MULTILINE_CODE ? LvnMode.TEXT : LvnMode.MULTILINE_CODE);
				} else {
					compiledModes[n] = mode;
					if (mode == LvnMode.CODE && line.startsWith("@")) {
						line = line.substring(1);
					}
				}

				if (mode == LvnMode.CODE || mode == LvnMode.MULTILINE_CODE) {
					line = parseCodeLine(line);
				} else {
					line = "";
				}
			} else if (mode == LvnMode.COMMENT || mode == LvnMode.MULTILINE_COMMENT) {
				if (line.startsWith("##")) {
					compiledModes[n] = LvnMode.MULTILINE_COMMENT;

					mode = (mode == LvnMode.MULTILINE_COMMENT ? LvnMode.TEXT : LvnMode.MULTILINE_COMMENT);
				} else {
					compiledModes[n] = mode;
				}

				//Ignore commented lines
				line = "";
			} else {
				throw new LvnParseException(filename, startLineNum+n, "Invalid mode: " + mode);
			}

			compiledLines[n] = line;
		}
	}

	protected String parseTextLine(String filename, String line, int textLineNum) {
		if (line.length() == 0) {
			return ""; //Empty line
		}

		List<String> out = new ArrayList<String>(8);
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
				//	endChar = '}';
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
					//	out.add(parseTextTag(str));
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
			if (sb.length() > 0) sb.append("; ");
			sb.append(outLine);
		}
		return sb.toString();
	}
	protected void parseTextLine_flush(Collection<String> out, StringBuilder sb) {
		if (sb.length() > 0) { //Flush buffered chars
			String ln = appendTextCommand(sb.toString());
			if (ln.length() > 0) out.add(ln);
			sb.delete(0, sb.length());
		}
	}

	protected String parseCodeLine(String line) {
		return line.trim();
	}

	protected String parseStringifier(String str) {
		return "paragraph.stringify(\"" + escape(str) + "\")";
	}

	protected String beginParagraphCommand(String filename, int textLineNum) {
		return "paragraph.start(\"" + escape(filename) + "\", " + textLineNum + ")";
	}
	protected String appendTextCommand(String line) {
		if (line.length() == 0) return "";
		line = ParserUtil.collapseWhitespace(escape(line), false);
		if (line.length() == 0) return "";
		return "paragraph.append(\"" + line + "\")";
	}
	protected String endParagraphCommand() {
		return "paragraph.finish()";
	}

}
