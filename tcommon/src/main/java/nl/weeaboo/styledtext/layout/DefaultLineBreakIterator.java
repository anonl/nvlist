package nl.weeaboo.styledtext.layout;

import java.text.BreakIterator;
import java.text.CharacterIterator;

class DefaultLineBreakIterator extends BreakIterator {

	private static final char[] DEFAULT_BREAKING = new char[] {
		0x200B, //Zero-width space
		'\n'    //Newline
	};
	
	private final BreakIterator charItr;
	private final char[] breakingChars;
	private CharacterIterator text;
	
	public DefaultLineBreakIterator(BreakIterator ci) {
		this(ci, DEFAULT_BREAKING);
	}
	public DefaultLineBreakIterator(BreakIterator ci, char[] breaking) {
		charItr = (BreakIterator)ci.clone();
		breakingChars = breaking;
		
		updateText();
	}
	
	//Functions
	private void updateText() {
		text = (CharacterIterator)charItr.getText().clone();		
	}
	
	public int align(int direction) {
		int offset = charItr.current();
		while (offset != DONE && !isBoundary(offset)) {
			offset = (direction >= 0 ? charItr.next() : charItr.previous());
		}
		return offset;
	}
	
	@Override
	public int first() {
		if (charItr.first() == DONE) {
			return DONE;
		}
		return align(1);
	}

	@Override
	public int last() {
		if (charItr.last() == DONE) {
			return DONE;
		}
		return align(-1);
	}

	@Override
	public int next() {
		return next(1);
	}

	@Override
	public int next(int n) {
		while (n > 0) {
			if (charItr.next() == DONE) {
				return DONE;
			}
			align(1);
			n--;
		}
		return charItr.current();
	}

	@Override
	public int previous() {
		if (charItr.previous() == DONE) {
			return DONE;
		}
		return align(-1);
	}

	@Override
	public int following(int offset) {
		if (charItr.following(offset) == DONE) {
			return DONE;
		}
		return align(1);
	}

	@Override
	public int current() {
		return charItr.current();
	}

	//Getters
	@Override
	public CharacterIterator getText() {
		return charItr.getText();
	}

	@Override
	public boolean isBoundary(int offset) {
		if (offset < text.getBeginIndex() || offset > text.getEndIndex()) {
			return false;
		}
		
		text.setIndex(offset);
		
		//WARNING: next()/previous() etc. Change the current position
		char prev = text.previous();
		if (prev == CharacterIterator.DONE) return true;
		
		char cur = text.next();
		
		//Break on certain chars
		for (char c : breakingChars) {
			if (cur == c || prev == c) return true;
		}
		
		//Boundary between whitespace and non-whitespace
		if (Character.isWhitespace(prev) != Character.isWhitespace(cur)) {
			return true;
		}
		
		return false;
	}
	
	//Setters
	@Override
	public void setText(CharacterIterator newText) {		
		charItr.setText(newText);
		updateText();
	}		

}
