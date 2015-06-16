package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

abstract class AbstractTextStyle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int[] NO_TAGS = new int[0];
	
	Map<TextAttribute, Object> properties;
	
	public AbstractTextStyle() {
		properties = new EnumMap<TextAttribute, Object>(TextAttribute.class);
	}
	
	//Functions
	static boolean atsEquals(AbstractTextStyle ats0, AbstractTextStyle ats1) {
		if (ats0 == null || ats1 == null) {
			return ats0 == ats1;
		}
		
		return ats0.properties.equals(ats1.properties);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		
		if (obj instanceof AbstractTextStyle) {
			return atsEquals(this, (AbstractTextStyle)obj);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return properties.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<TextAttribute, Object> entry : properties.entrySet()) {
			TextAttribute key = entry.getKey();
			String value = key.toString(entry.getValue());
			if (value != null) {
				if (sb.length() > 0) sb.append('|');
				sb.append(key + "=" + value);
			}
		}
		return sb.toString();
	}
	
	protected boolean addTag(int tag) {
		int[] oldTags = getTags();
		if (findValue(oldTags, tag) >= 0) {
			return false; //Already contains the tag
		}
		
		//Append the new tag to the list of tags
		int[] newTags = new int[oldTags.length+1];
		for (int n = 0; n < oldTags.length; n++) {
			newTags[n] = oldTags[n];
		}
		newTags[oldTags.length] = tag;
		properties.put(TextAttribute.tags, newTags);
		return true;
	}
	
	protected boolean removeTag(int tag) {
		int[] oldTags = getTags();
		if (oldTags == null) {
			return false;
		}
		
		//Count the number of matches
		int count = 0;
		for (int n = 0; n < oldTags.length; n++) {
			if (oldTags[n] == tag) count++;
		}
		
		if (oldTags.length <= count) {
			//No tags remain, remove the property alltogether
			properties.remove(TextAttribute.tags);
		} else {		
			//Walk through the old list, only copy non-matches to the new list
			int[] newTags = new int[oldTags.length-count];
			for (int s=0, d=0; s < oldTags.length; s++) {
				if (oldTags[s] != tag) {
					newTags[d++] = oldTags[s];
				}
			}
			properties.put(TextAttribute.tags, newTags);
		}		
		return count > 0;
	}
	
	//Getters
	public boolean hasProperty(TextAttribute key) {
		return properties.containsKey(key);
	}
	public Object getProperty(TextAttribute key, Object fallback) {
		Object val = properties.get(key);
		return (val != null ? val : fallback);
	}
	
	public String getFontName() { return getFontName(null); }
	public String getFontName(String fallback) {
		return (String)getProperty(TextAttribute.fontName, fallback);
	}
	public FontStyle getFontStyle() { return getFontStyle(FontStyle.PLAIN); }
	public FontStyle getFontStyle(FontStyle fallback) {
		return (FontStyle)getProperty(TextAttribute.fontStyle, fallback);
	}
	public float getFontSize() { return getFontSize(12f); }
	public float getFontSize(float fallback) {
		return (Float)getProperty(TextAttribute.fontSize, fallback);
	}
	public int getAnchor() { return getAnchor(0); }
	public int getAnchor(int fallback) {
		TextAnchor anchor = getAnchor(null);
		return (anchor != null ? anchor.intValue() : fallback);
	}
	public TextAnchor getAnchor(TextAnchor fallback) {
		return (TextAnchor)getProperty(TextAttribute.anchor, fallback);
	}
	public int getColor() { return getColor(0xFFFFFFFF); }
	public int getColor(int fallback) {
		return (Integer)getProperty(TextAttribute.color, fallback);
	}
	public boolean isUnderlined() { return isUnderlined(false); }
	public boolean isUnderlined(boolean fallback) {
		return (Boolean)getProperty(TextAttribute.underline, fallback);
	}
	public float getOutlineSize() { return getOutlineSize(0f); }
	public float getOutlineSize(float fallback) {
		return (Float)getProperty(TextAttribute.outlineSize, fallback);
	}
	public int getOutlineColor() { return getOutlineColor(0); }
	public int getOutlineColor(int fallback) {
		return (Integer)getProperty(TextAttribute.outlineColor, fallback);
	}
	public int getShadowColor() { return getShadowColor(0); }
	public int getShadowColor(int fallback) {
		return (Integer)getProperty(TextAttribute.shadowColor, fallback);
	}
	public float getShadowDx() { return getShadowDx(0f); }
	public float getShadowDx(float fallback) {
		return (Float)getProperty(TextAttribute.shadowDx, fallback);
	}
	public float getShadowDy() { return getShadowDy(0f); }
	public float getShadowDy(float fallback) {
		return (Float)getProperty(TextAttribute.shadowDy, fallback);
	}
	public float getSpeed() { return getSpeed(1f); }
	public float getSpeed(float fallback) {
		return (Float)getProperty(TextAttribute.speed, fallback);
	}	
	public int[] getTags() { return getTags(NO_TAGS); }
	public int[] getTags(int[] fallback) {
		return (int[])getProperty(TextAttribute.tags, fallback);
	}
	
	public boolean hasOutline() {
		return getOutlineSize() > 0.001f && ((getOutlineColor()>>24)&0xFF) != 0;
	}
	public boolean hasShadow() {
		return Math.abs(getShadowDx()) > 0.001f && Math.abs(getShadowDy()) > 0.001f
			&& ((getShadowColor()>>24)&0xFF) != 0;
	}
	public boolean hasTag(int tag) {
		return findValue(getTags(), tag) >= 0;		
	}
	
	static int findValue(int[] arr, int val) {
		if (arr != null) {
			for (int n = 0; n < arr.length; n++) {
				if (arr[n] == val) {
					return n;
				}
			}
		}
		return -1;
	}
	
	//Setters

}
