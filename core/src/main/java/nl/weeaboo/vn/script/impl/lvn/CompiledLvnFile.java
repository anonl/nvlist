package nl.weeaboo.vn.script.impl.lvn;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;

final class CompiledLvnFile implements ICompiledLvnFile {

	private final String filename;
    private final ImmutableList<LvnLine> lines;

    public CompiledLvnFile(String filename, List<LvnLine> lines) {
        this.filename = Checks.checkNotNull(filename);
        this.lines = ImmutableList.copyOf(lines);
	}

	@Override
	public int countTextLines(boolean countEmptyLines) {
		int count = 0;
        for (LvnLine line : lines) {
            if (line.getType() == LvnMode.TEXT) {
                if (countEmptyLines || !ParserUtil.isWhitespace(line.getSourceLine())) {
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
        for (LvnLine line : lines) {
            int lineWords = 0;

            if (line.getType() == LvnMode.TEXT) {
                String src = line.getSourceLine();
                if (!ParserUtil.isWhitespace(src)) {
                    wordBreakIterator.setText(src);

                    int index = wordBreakIterator.first();
                    while (index != BreakIterator.DONE) {
                        int lastIndex = index;
                        index = wordBreakIterator.next();
                        if (index != BreakIterator.DONE && ParserUtil.isWord(src, lastIndex, index)) {
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
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < lines.size(); n++) {
            sb.append(lines.get(n).getCompiledLine());
            sb.append('\n');
        }
        return sb.toString();
    }

}
