package nl.weeaboo.vn.script.lvn;

import java.text.BreakIterator;
import java.util.Locale;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;

class CompiledLvnFile implements ICompiledLvnFile {

	private final String filename;
	private final String[] srcLines;
	private final String[] compiledLines;
	private final LvnMode[] compiledModes;

	public CompiledLvnFile(String filename, String[] srcLines, String[] compiledLines,
			LvnMode[] compiledModes)
	{
	    Checks.checkArgument(srcLines.length == compiledLines.length, "source line count != compiled line count");
	    Checks.checkArgument(compiledLines.length == compiledModes.length, "compiled lines length != compiles modes length");

		this.filename = filename;
		this.srcLines = srcLines.clone();
		this.compiledLines = compiledLines.clone();
		this.compiledModes = compiledModes.clone();
	}

	@Override
	public int countTextLines(boolean countEmptyLines) {
		int count = 0;
		for (int n = 0; n < compiledLines.length; n++) {
			if (compiledModes[n] == LvnMode.TEXT) {
				if (countEmptyLines || !StringUtil.isWhitespace(srcLines[n])) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
    public int countTextWords() {
        return countTextWords(BreakIterator.getWordInstance(Locale.ROOT));
    }

    protected int countTextWords(BreakIterator wordBreakIterator) {
        int totalWords = 0;
        for (int n = 0; n < compiledLines.length; n++) {
            int lineWords = 0;

            String line = srcLines[n];
            if (compiledModes[n] == LvnMode.TEXT) {
                if (!ParserUtil.isWhitespace(line)) {
                    wordBreakIterator.setText(line);

                    int index = wordBreakIterator.first();
                    while (index != BreakIterator.DONE) {
                        int lastIndex = index;
                        index = wordBreakIterator.next();
                        if (index != BreakIterator.DONE && ParserUtil.isWord(line, lastIndex, index)) {
                            lineWords++;
                        }
                    }
                }
            }
            totalWords += lineWords;
        }
        return totalWords;
    }

	@Override
	public String getFilename() {
		return filename;
	}

    @Override
    public String getCompiledContents() {
        return ParserUtil.concatLines(compiledLines);
    }

}
