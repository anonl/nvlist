package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

public final class StyledText extends AbstractStyledText {

	private static final long serialVersionUID = 1L;
	
	public static final StyledText EMPTY_STRING = new StyledText();
	
	//--- Uses manual serialization, don't add variables ---
	
	/**
	 * For serialization only.
	 */
	@Deprecated
	public StyledText() {
		super(new char[0], new TextStyle[0]);
	}
	public StyledText(String text) {
		super(text.toCharArray(), new TextStyle[text.length()]);
	}
	public StyledText(String text, TextStyle style) {
		super(text.toCharArray(), replicate(style, text.length()));
	}
	public StyledText(char[] text, TextStyle style) {
		super(text.clone(), replicate(style, text.length));
	}
	public StyledText(char[] text, TextStyle[] styles) {
		super(text.clone(), styles.clone());
	}
	public StyledText(char[] text, TextStyle[] styles, int len) {
		super(copyOfRange(text, 0, len), copyOfRange(styles, 0, len));
	}
	
	/**
	 * Private constructor that omits defensive copying 
	 */
	StyledText(char[] text, int toff, TextStyle[] styles, int soff, int len) {	
		super(text, toff, styles, soff, len);
	}	
	
	//Functions
	public MutableStyledText mutableCopy() {
		return new MutableStyledText(getText(), 0, getStyles(), 0, length());
	}
	
	public StyledText concat(String text) {
		//TextStyle style = (length() > 0 ? getStyle(length()-1) : null);
		return concat(new StyledText(text));
	}
	public StyledText concat(StyledText... stexts) {
		int newLen = length();
		for (AbstractStyledText st : stexts) {
			newLen += st.length();
		}
		
		char[] newText = new char[newLen];
		TextStyle[] newStyles = new TextStyle[newLen];
		
		int t = length();
		getText(newText, 0, t);
		getStyles(newStyles, 0, t);
		for (AbstractStyledText st : stexts) {
			int stLen = st.length();
			st.getText(newText, t, stLen);
			st.getStyles(newStyles, t, stLen);
			t += stLen;
		}
		
		return new StyledText(newText, 0, newStyles, 0, newLen);
	}
			
	//Getters
	@Override
	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}
	
	public StyledText substring(int from) {
		return substring(from, length());
	}
	
	public StyledText substring(int from, int to) {
		if (to < from) throw new IllegalArgumentException("Can't have a substring of negative size, from=" + from + " to=" + to);
		
		checkBounds(from);
		if (to > from) checkBounds(to-1);
		
		return new StyledText(text, toff+from, styles, soff+from, to-from);
	}
	
	public CharacterIterator getCharacterIterator() {
		return getCharacterIterator(0, length());
	}
	public CharacterIterator getCharacterIterator(int from, int to) {
		if (from < 0 || to < from || to > length()) throw new IllegalArgumentException("Invalid substring, from=" + from + " to=" + to);
		
		return new CharArrayIterator(text, toff+from, to-from);
	}
	
	//Inner Classes
	private static class CharArrayIterator implements CharacterIterator {

	    private final char[] chars;
	    private final int off;
	    private final int len;
	    
	    private int pos;
	    
	    public CharArrayIterator(char[] chars, int off, int len) {	        
	        this.chars = chars;
	        this.off = off;
	        this.len = len;
	    }
	    	    
	    @Override
	    public Object clone() {
	        CharArrayIterator c = new CharArrayIterator(chars, off, len);
	        c.pos = pos;
	        return c;
	    }
	    
	    @Override
	    public char current() {
			if (pos >= 0 && pos < len) {
				return chars[off + pos];
			}
			return DONE;
	    }

	    @Override
	    public char first() {	        
	        pos = 0;
	        return current();
	    }

	    @Override
		public char last() {
			pos = (len > 0 ? len-1 : 0);
			return current();
		}
	    
	    @Override
	    public char previous() {
			if (pos > 0) {
				pos--;
				return chars[off + pos];
			}
			pos = 0;
			return DONE;
	    }

	    @Override
	    public char next() {
			if (pos < len - 1) {
				pos++;
				return chars[off + pos];
			}
			pos = len;
			return DONE;
	    }

	    @Override
		public char setIndex(int position) {
			if (position < 0 || position > len) {
				throw new IllegalArgumentException("Invalid index");
			}
			pos = position;
			return current();
		}

	    @Override
	    public int getBeginIndex() {
	        return 0;
	    }

	    @Override
	    public int getEndIndex() {
	        return len;
	    }

	    @Override
	    public int getIndex() {
	        return pos;
	    }

	}
	
}
