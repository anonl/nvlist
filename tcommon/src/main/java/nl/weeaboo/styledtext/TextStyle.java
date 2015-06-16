package nl.weeaboo.styledtext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class TextStyle extends AbstractTextStyle {
	
	private static final long serialVersionUID = 1L;

	private static final TextStyle DEFAULT_INSTANCE = new TextStyle();
	
	protected TextStyle() {
		super();		
	}
	public TextStyle(String fontName, FontStyle fontStyle, float fontSize) {
		this();
		
		if (fontName != null) properties.put(TextAttribute.fontName, fontName);
		properties.put(TextAttribute.fontStyle, fontStyle);
		properties.put(TextAttribute.fontSize, fontSize);
	}
	
	TextStyle(AbstractTextStyle m) {
		properties.putAll(m.properties);
	}
	
	//Functions
	public static TextStyle defaultInstance() {
		return DEFAULT_INSTANCE;
	}
	public static TextStyle withTags(int... tags) {
		TextStyle ts = new TextStyle();
		for (int tag : tags) {
			ts.addTag(tag);
		}
		return ts;
	}
	
	/**
	 * Returns the horizontal direction of the given anchor in the specified default text direction.
	 * @return <code>-1</code> if left-aligned, <code>0</code> if center-aligned, <code>1</code> if right-aligned.
	 */
	public static int getHorizontalAlign(int anchor, boolean isRightToLeft) {
		if (anchor == 0) {
			return (isRightToLeft ? 1 : -1);
		}
		return ((anchor-1) % 3) - 1;
	}
	
	public MutableTextStyle mutableCopy() {
		return new MutableTextStyle(this);
	}
	public TextStyle extend(TextStyle ts) {
		return extend(this, ts);
	}
	
	public static TextStyle extend(TextStyle base, TextStyle ext) {
		if (base == null || base.equals(ext)) {
			return ext;
		} else if (ext == null) {
			return base;
		}
		
		TextStyle result = new TextStyle(base);
		for (Entry<TextAttribute, Object> entry : ext.properties.entrySet()) {
			TextAttribute key = entry.getKey();
			Object oldval = base.properties.get(key);
			Object extval = entry.getValue();
			result.properties.put(key, key.extendValue(oldval, extval));
		}
		return result;
	}
	
	public static void extend(TextStyle[] out, TextStyle[] base, TextStyle[] ext) {
		extend(out, 0, base, 0, ext, 0, Math.min(base.length, ext.length));
	}
	
	public static void extend(TextStyle[] out, int roff, TextStyle[] base, int boff, TextStyle[] ext, int eoff, int len) {
		Map<ReplaceKey, TextStyle> replace = new HashMap<ReplaceKey, TextStyle>();
		
		int r = roff, b = boff, e = eoff;
		ReplaceKey lastRKey = null;
		TextStyle lastRVal = null;
		for (int n = 0; n < len; n++) {
			TextStyle extended;
			if (base[b] == null) {
				extended = ext[e];
			} else if (ext[e] == null) {
				extended = base[b];
			} else {
				if (lastRKey != null && lastRKey.base == base[b] && lastRKey.ext == ext[e]) {
					extended = lastRVal;
				} else {
					ReplaceKey rkey = new ReplaceKey(base[b], ext[e]);
					extended = replace.get(rkey);
					if (extended == null) {
						extended = TextStyle.extend(base[b], ext[e]);
						replace.put(rkey, extended);
					}
					
					lastRKey = rkey;
					lastRVal = extended;
				}
			}
			out[r] = extended;
			
			r++; b++; e++;
		}
	}
	
	public boolean equals(TextStyle ts) {
		if (this == ts) return true;

		return atsEquals(this, ts);
	}
	public boolean equals(MutableTextStyle mts) {
		return atsEquals(this, mts);
	}
	
	//Getters
	
	//Setters
	
	//Save Support
	public static TextStyle fromString(String string) {
		if (string == null) return null;
		
		TextStyle style = new TextStyle();
		
		String parts[] = string.split("\\|");
		for (int n = 0; n < parts.length; n++) {			
			int index = parts[n].indexOf('=');			

			TextAttribute key = null;
			if (index < 0) {
				TextAttribute keys[] = TextAttribute.values();
				if (n >= 0 && n < keys.length) {
					key = keys[n];
				} 
			} else {
				String keyS = parts[n].substring(0, index).trim();
				key = TextAttribute.valueOf(keyS);
			}
			
			if (key != null) {
				String valueS = parts[n].substring(index+1).trim();
				if (valueS.length() > 0) {
					Object value = key.fromString(valueS);
					if (value != null) {
						style.properties.put(key, value);
					}
				}
			}
		}
		
		return style;
	}
	
	//Inner Classes
	private static class ReplaceKey {
		
		public final TextStyle base;
		public final TextStyle ext;
		
		public ReplaceKey(TextStyle b, TextStyle e) {
			base = b;
			ext = e;
			
			if (base == null || ext == null) {
				throw new IllegalArgumentException("Both base and ext must be non-null");
			}
		}
		
		@Override
		public int hashCode() {
			return (base.hashCode()&0xFFFF)|(ext.hashCode()<<16);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ReplaceKey) {
				ReplaceKey r = (ReplaceKey)obj;
				return base.equals(r.base)
					|| ext.equals(r.ext);
			}
			return false;
		}
		
	}
			
}
