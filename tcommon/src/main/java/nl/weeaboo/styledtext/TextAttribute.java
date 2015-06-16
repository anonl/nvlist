package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import nl.weeaboo.common.StringUtil;

public enum TextAttribute {

	fontName(String.class),
	fontStyle(FontStyle.class),
	fontSize(Float.class),
	anchor(TextAnchor.class),
	color(Integer.class),
	underline(Boolean.class),
	outlineSize(Float.class),
	outlineColor(Integer.class),
	shadowColor(Integer.class),
	shadowDx(Float.class),
	shadowDy(Float.class),
	speed(Float.class),
	tags(int[].class);
		
	private Class<?> type;
	
	private TextAttribute(Class<? extends Serializable> type) {
		this.type = type;
	}
	
	//Function	
	public Class<?> getType() {
		return type;
	}
    
	public boolean isValidType(Object val) {
		return val == null || type.isAssignableFrom(val.getClass());
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	private static Object cloneValue(Object obj) {
		if (obj == null) return null;
		
		if (obj instanceof int[]) {
			return ((int[])obj).clone();
		}
	
		return obj;
	}
	
	public Object extendValue(Object base, Object ext) {
		if (base == null) return cloneValue(ext);
		if (ext == null) return cloneValue(base);
		
		if (this == fontStyle) {
			FontStyle oldStyle = (FontStyle)base;
			FontStyle extStyle = (FontStyle)ext;
			if (oldStyle == FontStyle.BOLD && extStyle == FontStyle.ITALIC) {
				return FontStyle.BOLDITALIC;
			} else if (oldStyle == FontStyle.ITALIC && extStyle == FontStyle.ITALIC) {
				return FontStyle.BOLDITALIC;
			}
		} else if (this == tags) {
			int[] baseTags = (int[])base;
			int[] extTags = (int[])ext;
			
			Set<Integer> merged = new TreeSet<Integer>();
			for (int i : baseTags) merged.add(i);
			for (int i : extTags) merged.add(i);
			
			int[] result = new int[merged.size()];
			int t = 0;
			for (Integer i : merged) {
				result[t++] = i;
			}
			return result;
		}
		
		return cloneValue(ext);
	}
	
	public Object fromString(String string) {
		if (string == null) return null;
		
		if (this == color || this == outlineColor || this == shadowColor) {
			int val = (int)(Long.parseLong(string, 16) & 0xFFFFFFFFL);
			if (string.length() < 8) {
				val = 0xFF000000|(val);
			}
			return val;
		} else if (this == fontName) {
			return string.toLowerCase();
		} else if (this == tags) {
			String[] parts = string.split(",");
			int[] result = new int[parts.length];
			for (int n = 0; n < result.length; n++) {
				try {
					result[n] = Integer.parseInt(parts[n]);
				} catch (NumberFormatException nfe) {
					//Ignore (leaves result at 0)
				}
			}
			return result;
		}
		
		if (type == FontStyle.class) {
			return FontStyle.fromString(string);
		} else if (type == TextAnchor.class) {
			return TextAnchor.fromString(string);
		}
		
		try {
			if (type == Boolean.class) {
				return Boolean.parseBoolean(string);
			} else if (type == Byte.class) {
				return Byte.parseByte(string);
			} else if (type == Short.class) {
				return Short.parseShort(string);
			} else if (type == Integer.class) {
				return Integer.parseInt(string);
			} else if (type == Long.class) {
				return Long.parseLong(string);
			} else if (type == Float.class) {
				return Float.parseFloat(string);
			} else if (type == Double.class) {
				return Double.parseDouble(string);
			} else if (type == String.class) {
				return string;
			}
		} catch (NumberFormatException nfe) {
			//Ignore
		}
				
		return null;
	}
	
	public String toString(Object val) {
		if (val == null) return null;
		
		if (this == color || this == outlineColor || this == shadowColor) {
			return StringUtil.formatRoot("%08x", (Integer)val);
		} else if (this == fontName) {
			return String.valueOf(val).toLowerCase();
		} else if (this == tags) {
			StringBuilder sb = new StringBuilder();
			for (int i : (int[])val) {
				if (sb.length() > 0) sb.append(',');
				sb.append(i);
			}
			return sb.toString();
		}
		
		return String.valueOf(val);
	}
	
}
