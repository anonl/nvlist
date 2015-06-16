package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class TextLayout implements Iterable<LineLayout> {

	private List<LineLayout> lines;
	private float lineSpacing;
	private float reservedWidth;
	private boolean sealed;
	
	public TextLayout(float linespc) {
		lines = new ArrayList<LineLayout>(4);
		lineSpacing = linespc;
	}
	
	//Functions
	public void addLine(LineLayout line) {
		lines.add(line);
	}
	public void removeLine(LineLayout line) {
		lines.remove(line);
	}
	
	public void seal(float width) {
		if (!sealed) {
			if (width < 0) {
				width = 0;
				for (LineLayout line : lines) {
					width = Math.max(width, line.getRight());
				}
			}
						
			for (LineLayout line : lines) {
				line.seal(width);
			}
			
			reservedWidth = width;
			sealed = true;
		}
	}

	@Override
	public Iterator<LineLayout> iterator() {
		return lines.iterator();
	}

	public Iterator<Word> wordIterator() {
		return new WordIterator(lines);
	}
	
	//Getters
	public int getNumLines() { return lines.size(); }
	public LineLayout getLine(int line) { return lines.get(line); }
	public float getLineSpacing() { return lineSpacing; }
	
	public int getCharOffset(int line) {
		int chars = 0;
		for (int n = 0; n < line; n++) {
			chars += getLine(n).getGlyphCount();
		}
		return chars;
	}
	
	public int getNumChars() {
		return getCharOffset(getNumLines());
	}
	
	public float getWidth() {
		if (reservedWidth >= 0) {
			return reservedWidth;
		}
		
		float width = 0;
		for (int n = 0; n < lines.size(); n++) {
			LineLayout line = lines.get(n);
			width = Math.max(width, line.getPaddedWidth());
		}
		return width;
	}
	
	public float getHeight() {
		return getHeight(0, getNumLines());
	}		
	public float getHeight(int start, int end) {
		float y = lineSpacing * Math.max(0, end-start-1);
		for (int n = start; n < end; n++) {
			y += lines.get(n).getHeight();
		}
		return y;
	}
	
	public float getLineTop(int index) {
		float y = lineSpacing * Math.max(0, index-1);
		for (int n = 0; n < index; n++) {
			y += lines.get(n).getHeight();
		}
		return y;
	}
	
	public float getLineBottom(int index) {
		float y = getLineTop(Math.min(getNumLines(), index));
		if (index < getNumLines()) {
			y += getLine(index).getHeight();
		}
		return y;
	}
	
	public float getPadLeft(int index) {
		return lines.get(index).getPadLeft();
	}
	public float getPadRight(int index) {
		return lines.get(index).getPadRight();
	}
	public float getLineLeft(int index) {
		return lines.get(index).getLeft();
	}
	public float getLineRight(int index) {
		return lines.get(index).getRight();
	}
	public float getLineWidth(int index) {
		return lines.get(index).getWidth();
	}
	
	public int getLineIndexAt(float cy) {
		float y = 0;
		for (int n = 0; n < getNumLines(); n++) {
			LineLayout ll = getLine(n);
			float lh = ll.getHeight();
			if (cy >= y && cy < y+lh) {
				return n;
			}
			y += lh + lineSpacing;
		}
		return -1;
	}
	
	//Setters
	
	//Inner Classes
	private static final class WordIterator implements Iterator<Word> {
		
		private final List<LineLayout> lines;
		private int lineIndex;
		private int wordIndex;
		
		public WordIterator(List<LineLayout> lines) {
			this.lines = lines;
		}
		
		@Override
		public boolean hasNext() {
			return skipToNext();
		}
		
		@Override
		public Word next() {
			if (!skipToNext()) {
				throw new NoSuchElementException();
			}
			Word word = (Word)lines.get(lineIndex).getElement(wordIndex);
			wordIndex++;
			return word;
		}
		
		private boolean skipToNext() {
			while (lineIndex < lines.size()) {
				LineLayout ll = lines.get(lineIndex);
				while (wordIndex < ll.getElementCount() && !ll.getElement(wordIndex).isWord()) {
					wordIndex++;
				}				
				if (wordIndex >= ll.getElementCount()) {
					//Need to continue searching the next line
					lineIndex++;
					wordIndex = 0;
				} else {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void remove() {
			throw new RuntimeException("remove() not supported");
		}
		
	}
	
}
