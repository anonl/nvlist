package nl.weeaboo.vn.script.lvn;

import static nl.weeaboo.lua2.LuaUtil.escape;
import static nl.weeaboo.lua2.LuaUtil.unescape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.vn.script.lvn.TextParser.Token;

class LvnParser4 implements ILvnParser {

	private final TextParser textParser;

	private String filename;
	private String[] sourceLines;
	private String[] compiledLines;
	private LvnMode[] compiledModes;

	public LvnParser4() {
		textParser = new TextParser();
	}

	//Functions
	private void init(String filename, InputStream in) throws IOException {
        String[] lines = ParserUtil.readLinesUtf8(in);

        this.filename = filename;
        this.sourceLines = lines;
        this.compiledLines = new String[lines.length];
        this.compiledModes = new LvnMode[lines.length];
	}

	private void reset() {
	    filename = null;
	    sourceLines = null;
	    compiledLines = null;
	    compiledModes = null;
	}

	@Override
	public CompiledLvnFile parseFile(String filename, InputStream in) throws LvnParseException, IOException {
	    init(filename, in);

		doParseFile(1, 1);

        // Return result and clear internal state
		CompiledLvnFile result = new CompiledLvnFile(filename, sourceLines, compiledLines, compiledModes);
		reset();
		return result;
	}

	private void doParseFile(int startLineNum, int textLineNum) throws LvnParseException {
		LvnMode mode = LvnMode.TEXT;
		StringBuilder temp = new StringBuilder();
		for (int n = 0; n < sourceLines.length; n++) {
			String line = sourceLines[n].trim();

			//End single-line modes
			if (mode.isSingleLine()) mode = LvnMode.TEXT;

			//Look for comment starts
			if (mode == LvnMode.TEXT && line.startsWith("#")) mode = LvnMode.COMMENT;
			if (mode == LvnMode.TEXT && line.startsWith("@")) mode = LvnMode.CODE;

			//Process line
			if (mode == LvnMode.TEXT) {
				compiledModes[n] = mode;
				if (line.length() > 0) {
					parseTextLine(temp, filename, line, textLineNum);
					line = temp.toString();
					temp.delete(0, temp.length());
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

	protected void parseTextLine(StringBuilder out, String filename, String line, int textLineNum) {
		if (line.length() == 0) {
			return; //Empty line
		}

		line = unescape(line);

		List<String> triggers = new ArrayList<String>(4);
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
		out.append("filename=\"").append(escape(filename)).append("\", ");
		out.append("line=").append(textLineNum).append(",");
		out.append("}");

		out.append(")");
	}

	protected String parseCodeLine(String line) {
		return line.trim();
	}

}
