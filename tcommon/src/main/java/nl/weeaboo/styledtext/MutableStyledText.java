package nl.weeaboo.styledtext;

import java.util.Arrays;

public final class MutableStyledText extends AbstractStyledText {
	
	private static final long serialVersionUID = 1L;
	
	//--- Uses manual serialization, don't add variables ---
	
	public MutableStyledText() {
		super(new char[0], new TextStyle[0]);
	}
	public MutableStyledText(String text) {
		super(text.toCharArray(), new TextStyle[text.length()]);
	}
	public MutableStyledText(String text, TextStyle style) {
		super(text.toCharArray(), replicate(style, text.length()));
	}
	public MutableStyledText(char[] text, TextStyle style) {
		super(text.clone(), replicate(style, text.length));
	}
	public MutableStyledText(char[] text, TextStyle[] styles) {
		this(text, 0, styles, 0, Math.min(text.length, styles.length));
	}		
	public MutableStyledText(char[] text, int toff, TextStyle[] styles, int soff, int len) {	
		super(copyOfRange(text, toff, len), copyOfRange(styles, soff, len));
	}	
	
	//Functions
	public StyledText immutableCopy() {
		return new StyledText(text, toff, styles, soff, len);
	}
	
	public void append(AbstractStyledText append) {
		int newLen = length() + append.length();	
		if (capacity() < newLen) {
			ensureCapacity(newLen);
		}
		
		append.getText(text, toff+len, append.length());
		append.getStyles(styles, soff+len, append.length());
		len = newLen;
	}
	
	public void append(char c, TextStyle style) {
		int newLen = len + 1;	
		if (capacity() < newLen) {
			ensureCapacity(newLen);
		}
		
		text[toff+len] = c;
		styles[soff+len] = style;
		len = newLen;
	}
	
	private void ensureCapacity(int targetCapacity) {
		int capacity = capacity();
		if (capacity >= targetCapacity) {
			return;
		}
		
		targetCapacity = Math.max(capacity + 16, targetCapacity);
		
		char[] newText = new char[targetCapacity];
		TextStyle[] newStyles = new TextStyle[targetCapacity];
		
		int t = length();
		getText(newText, 0, t);
		getStyles(newStyles, 0, t);
		
		text = newText;
		toff = 0;
		styles = newStyles;
		soff = 0;
	}
	
	private int capacity() {
		return Math.min(text.length - toff, styles.length - soff);
	}
	
	public void extendStyle(TextStyle ext) {
		extendStyle(replicate(ext, styles.length));
	}
	public void extendStyle(int from, int to, TextStyle ext) {
		extendStyle(from, to, replicate(ext, to-from), 0);
	}
	public void extendStyle(TextStyle[] ext) {
		extendStyle(0, styles.length, ext);
	}
	public void extendStyle(int from, int to, TextStyle[] ext) {
		extendStyle(from, to, ext, 0);
	}
	public void extendStyle(int from, int to, TextStyle[] ext, int eoff) {	
		TextStyle.extend(styles, from, styles, from, ext, eoff, to-from);
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		final int L = length();
		if (start < 0 || end < start || end > L) {
			throw new IndexOutOfBoundsException("Invalid subSequence: " + start + "-" + end);
		}
		return new MutableStyledText(text, toff+start, styles, soff+start, end-start);
	}
	
	//Getters
	
	//Setters
	public void setStyle(TextStyle style) {
		setStyle(style, 0, text.length);
	}
	public void setStyle(TextStyle style, int index) {
		setStyle(style, index, index+1);
	}
	public void setStyle(TextStyle style, int from, int to) {
		if (from+1 == to) {
			styles[from] = style;
		} else {
			Arrays.fill(styles, from, to, style);
		}
	}

	public void setBaseStyle(TextStyle base) {
		setBaseStyle(replicate(base, styles.length));
	}
	public void setBaseStyle(int from, int to, TextStyle base) {
		setBaseStyle(from, to, replicate(base, to-from), 0);
	}
	public void setBaseStyle(TextStyle[] base) {
		setBaseStyle(0, styles.length, base);
	}
	public void setBaseStyle(int from, int to, TextStyle[] base) {
		setBaseStyle(from, to, base, 0);
	}
	public void setBaseStyle(int from, int to, TextStyle[] base, int boff) {	
		TextStyle.extend(styles, from, base, boff, styles, from, to-from);
	}
	
}
