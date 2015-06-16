package nl.weeaboo.styledtext.layout;

import java.text.Bidi;
import java.text.BreakIterator;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

final class TextSplitter {
	
	private final RunHandler runHandler;
	private final StyledText str;
	private final BreakIterator wordIterator;
	private final Bidi bidi;
	
	private int index;
	private int nextWordBoundary;
	private int runStart;
	private TextStyle style;
	private int bidiLevel;
	private boolean whitespace;
	private boolean isBoundary;
	private boolean isLineBreak;
	
	private TextSplitter(RunHandler handler, StyledText str, BreakIterator bitr, boolean isRightToLeft) {
		this.runHandler = handler;
		this.str = str;
		this.wordIterator = bitr;
		
		wordIterator.setText(str.getCharacterIterator());
		if (str.isBidi() || isRightToLeft) {
			bidi = str.getBidi(isRightToLeft ? Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT : Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
		} else {
			bidi = null;
		}
		
		whitespace = true;
	}
	
	public static void run(RunHandler handler, StyledText str, BreakIterator lineIterator, boolean isRightToLeft) {
		TextSplitter ss = new TextSplitter(handler, str, lineIterator, isRightToLeft);
		while (!ss.isDone()) {
			ss.readCodepoint();
		}
	}
			
	private int nextPos() {
		if (index < str.length()-1) {
			index++;
			return index;
		}
		index = str.length();
		return BreakIterator.DONE;
	}
	
	public int readCodepoint() {
		final TextStyle oldStyle = style;
		final int oldBidiLevel = bidiLevel;
		final boolean oldWhitespace = whitespace;
		final int i = index;
		
		style = str.getStyle(i);
		bidiLevel = (bidi != null ? bidi.getLevelAt(i) : 0);
		
		char c = str.charAt(i);
		nextPos();
		
		int codepoint;
		if (Character.isHighSurrogate(c) && !isDone()) {
			codepoint = Character.toCodePoint(c, str.charAt(i+1));
			nextPos();
		} else {
			codepoint = c;
		}
		
		whitespace = Character.isWhitespace(codepoint) && !LayoutUtil.isNonBreakingSpace(codepoint);
		boolean newLineBreak = (codepoint == '\n');
		isBoundary = isLineBreak
				|| newLineBreak
				|| (nextWordBoundary >= i && nextWordBoundary < index)
				|| (whitespace != oldWhitespace)
				|| (style != oldStyle)
				|| (bidiLevel != oldBidiLevel);
		
		//Process the current run if we're at its end boundary
		if (isBoundary) {
			if (i > runStart || isLineBreak) {
				runHandler.processRun(str, runStart, i, oldStyle, oldWhitespace, isLineBreak, oldBidiLevel);
			}
			runStart = i;
			isLineBreak = false;
		}
				
		//Go look for the next word boundary if needed
		while (nextWordBoundary != BreakIterator.DONE && index > nextWordBoundary) {
			nextWordBoundary = wordIterator.next();
		}
		
		//Update whether or not this run contains a newline
		isLineBreak = isLineBreak || newLineBreak;
		
		//Process the final pending run if otherwise finished
		if (isDone()) {
			if (str.length() > runStart || isLineBreak) {
				runHandler.processRun(str, runStart, str.length(), style, whitespace, isLineBreak, bidiLevel);
			}
			runStart = str.length();
		}
		return codepoint;
	}
	
	public boolean isDone() { return index < 0 || index >= str.length(); }
	public int getPos() { return index; }
	public boolean isBoundary() { return isBoundary; }
	public int getBidiLevel() { return bidiLevel; }		
	public boolean isWhitespace() { return whitespace; }
	public boolean isLineBreak() { return isLineBreak; }
	public TextStyle getStyle() { return style; }		
	
	public interface RunHandler {
		
		void processRun(StyledText str, int start, int end, TextStyle style, boolean isWhitespace,
				boolean isLineBreak, int bidiLevel);
		
	}
	
}