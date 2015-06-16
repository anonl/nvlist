package nl.weeaboo.styledtext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.Bidi;
import java.util.Arrays;

abstract class AbstractStyledText implements Externalizable, CharSequence {
	
	private static final long serialVersionUID = 1L;
	
	//--- Uses manual serialization, don't add variables ---
	protected int len;
	protected char[] text;
	protected int toff;
	protected TextStyle[] styles;
	protected int soff;
	//--- Uses manual serialization, don't add variables ---
		
	protected AbstractStyledText(char[] t, TextStyle[] s) {
		this(t, 0, s, 0, Math.min(t.length, s.length));
	}
	protected AbstractStyledText(char[] t, int toff, TextStyle[] s, int soff, int len) {	
		if (t == null || s == null) throw new NullPointerException();
		
		this.text = t;
		this.toff = toff;
		this.styles = s;
		this.soff = soff;
		this.len = len;
	}
	
	//Functions
	protected static TextStyle[] replicate(TextStyle ts, int times) {
		TextStyle result[] = new TextStyle[times];
		if (ts != null) Arrays.fill(result, ts);
		return result;
	}
	
	static char[] copyOfRange(char arr[], int from, int to) {
		char result[] = new char[to-from];
		if (to > from) System.arraycopy(arr, from, result, 0, result.length);
		return result;
	}
	
	static TextStyle[] copyOfRange(TextStyle arr[], int from, int to) {
		TextStyle result[] = new TextStyle[to-from];
		if (to > from) System.arraycopy(arr, from, result, 0, result.length);
		return result;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(len);
		
		out.writeInt(toff);
		out.writeUTF(new String(text, toff, len));
		
		out.writeInt(soff);
		TextStyle last = null;
		int lastIndex = 0;
		for (int n = 0; n < len; n++) {
			if (styles[n] != last) {
				out.writeInt(n - lastIndex);
				out.writeObject(last);
				last = styles[n];
				lastIndex = n;
			}
		}
		if (lastIndex < len) {
			out.writeInt(len - lastIndex);			
			out.writeObject(last);
		}
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		len = in.readInt();
		
		toff = in.readInt();
		text = in.readUTF().toCharArray();
		
		soff = in.readInt();
		styles = new TextStyle[len];
		int s = 0;
		while (s < len) {
			int count = in.readInt();
			Arrays.fill(styles, in.readObject());
			s += count;
		}
	}
	
	@Override
	public int hashCode() {
		return len;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof AbstractStyledText) {
			AbstractStyledText stext = (AbstractStyledText)other;
			if (length() != stext.length()) {
				return false;
			}
			
			for (int n = 0; n < len; n++) {
				int c0 = getChar(n);
				int c1 = stext.getChar(n);
				if (c0 != c1) {
					return false;
				}
				
				TextStyle s0 = getStyle(n);
				TextStyle s1 = stext.getStyle(n);
				if (s0 != s1 && (s0 == null || !s0.equals(s1))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return new String(text, toff, len);
	}
	
	protected final void checkBounds(int index) {
		if (index < 0 || index >= len) {
			throw new ArrayIndexOutOfBoundsException("Index=" + index);		
		}
	}
	
	@Override
	public char charAt(int index) {
		checkBounds(index);
		return text[toff+index];
	}
	
	//Getters
	public boolean isBidi() {
		return Bidi.requiresBidi(text, toff, len);
	}
	
	public Bidi getBidi(int dirFlags) {
		return new Bidi(text, toff, null, 0, len, dirFlags);
	}
	
	@Override
	public int length() {
		return len;
	}
	
	public char getChar(int index) {
		checkBounds(index);
		return text[toff+index];
	}
	public char[] getText() {
		char[] out = new char[len];
		getText(out, 0, out.length);
		return out;
	}
	public int getText(char[] out, int off, int len) {
		int lim = Math.min(this.len, len);
		System.arraycopy(text, toff, out, off, lim);
		return lim;
	}
	
	public TextStyle getStyle(int index) {
		checkBounds(index);
		return styles[soff+index];
	}
	public TextStyle[] getStyles() {
		TextStyle[] out = new TextStyle[len];
		getStyles(out, 0, out.length);
		return out;
	}
	public int getStyles(TextStyle out[], int off, int len) {
		int lim = Math.min(this.len, len);
		System.arraycopy(styles, soff, out, off, lim);
		return lim;
	}
	
	//Setters

}
