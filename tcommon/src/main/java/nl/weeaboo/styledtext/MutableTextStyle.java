package nl.weeaboo.styledtext;

import java.util.Map.Entry;

public final class MutableTextStyle extends AbstractTextStyle {

	private static final long serialVersionUID = 1L;

	public MutableTextStyle() {
		super();
	}
	public MutableTextStyle(String fontName, FontStyle fontStyle, float fontSize) {
		this();
		
		properties.put(TextAttribute.fontName, fontName);
		properties.put(TextAttribute.fontStyle, fontStyle);
		properties.put(TextAttribute.fontSize, fontSize);
	}

	MutableTextStyle(AbstractTextStyle m) {
		properties.putAll(m.properties);
	}
	
	//Functions
	public MutableTextStyle copy() {
		return new MutableTextStyle(this);
	}
	public TextStyle immutableCopy() {
		return new TextStyle(this);
	}
	
	protected static int packColor(float r, float g, float b, float a) {
		int ri = Math.max(0, Math.min(255, Math.round(255 * r)));
		int gi = Math.max(0, Math.min(255, Math.round(255 * g)));
		int bi = Math.max(0, Math.min(255, Math.round(255 * b)));
		int ai = Math.max(0, Math.min(255, Math.round(255 * a)));
		return (ai<<24)|(ri<<16)|(gi<<8)|(bi);
	}

	public void extend(TextStyle ts) {
		if (ts == null || equals(ts)) return;

		for (Entry<TextAttribute, Object> entry : ts.properties.entrySet()) {
			TextAttribute key = entry.getKey();
			Object oldval = properties.get(key);
			Object extval = entry.getValue();
			properties.put(key, key.extendValue(oldval, extval));
		}
	}

	public boolean equals(TextStyle ts) {
		return atsEquals(this, ts);
	}
	public boolean equals(MutableTextStyle mts) {
		if (this == mts) return true;
		
		return atsEquals(this, mts);
	}
	
	public void removeProperty(TextAttribute key) {
		properties.remove(key);
	}
	
	@Override
	public boolean addTag(int tag) {
		return super.addTag(tag);
	}
	
	@Override
	public boolean removeTag(int tag) {
		return super.removeTag(tag);
	}
	
	//Getters
	
	//Setters
	public void setProperty(TextAttribute key, Object value) {
		if (key == TextAttribute.fontName) {
			value = (value != null ? String.valueOf(value).toLowerCase() : null);
		} else if (key == TextAttribute.fontStyle) {
			if (value instanceof String) {
				value = FontStyle.fromString((String)value);
			} else if (value instanceof Integer) {
				value = FontStyle.fromInt((Integer)value);
			}
		} else if (key == TextAttribute.anchor) {
			if (value instanceof String) {
				value = TextAnchor.fromString((String)value);
			} else if (value instanceof Integer) {
				value = TextAnchor.fromInt((Integer)value);
			}			
		} else if (key == TextAttribute.tags) {
			if (value instanceof int[]) {
				int[] arr = (int[])value;
				if (arr.length == 0) {
					removeProperty(TextAttribute.tags);
					return;
				}
			}
		}
		
		if (key.isValidType(value)) {
			properties.put(key, value);
		}
	}
	
	public void setFontName(String f) {
		setProperty(TextAttribute.fontName, f);
	}
	public void setFontStyle(FontStyle s) {
		setProperty(TextAttribute.fontStyle, s);
	}
	public void setFontSize(float s) {
		setProperty(TextAttribute.fontSize, s);
	}
	public void setFont(String fn, FontStyle fs, float sz) {
		setFontName(fn);
		setFontStyle(fs);
		setFontSize(sz);
	}
	public void setColor(float r, float g, float b) {
		setColor(r, g, b, 1);
	}
	public void setColor(float r, float g, float b, float a) {		
		setColor(packColor(r, g, b, a));
	}
	public void setColor(int r, int g, int b) {
		setColor(r, g, b, 255);
	}
	public void setColor(int r, int g, int b, int a) {
		setColor(((a<<24)&0xFF000000)|((r<<16)&0xFF0000)|((g<<8)&0xFF00)|(b&0xFF));
	}
	public void setColor(int argb) {
		setProperty(TextAttribute.color, argb);
	}
	public void setAnchor(int a) {
		setAnchor(TextAnchor.fromInt(a));
	}
	public void setAnchor(TextAnchor a) {
		setProperty(TextAttribute.anchor, a);
	}
	public void setUnderlined(boolean u) {
		setProperty(TextAttribute.underline, u);
	}
	public void setOutlineSize(float s) {
		setProperty(TextAttribute.outlineSize, s);
	}
	public void setOutlineColor(float r, float g, float b) {
		setOutlineColor(r, g, b, 1);
	}
	public void setOutlineColor(float r, float g, float b, float a) {		
		setOutlineColor(packColor(r, g, b, a));
	}
	public void setOutlineColor(int r, int g, int b) {
		setOutlineColor(r, g, b, 255);
	}
	public void setOutlineColor(int r, int g, int b, int a) {
		setOutlineColor(((a<<24)&0xFF000000)|((r<<16)&0xFF0000)|((g<<8)&0xFF00)|(b&0xFF));
	}
	public void setOutlineColor(int argb) {
		setProperty(TextAttribute.outlineColor, argb);
	}
	public void setShadowColor(float r, float g, float b) {
		setShadowColor(r, g, b, 1);
	}
	public void setShadowColor(float r, float g, float b, float a) {		
		setShadowColor(packColor(r, g, b, a));
	}
	public void setShadowColor(int r, int g, int b) {
		setShadowColor(r, g, b, 255);
	}
	public void setShadowColor(int r, int g, int b, int a) {
		setShadowColor(((a<<24)&0xFF000000)|((r<<16)&0xFF0000)|((g<<8)&0xFF00)|(b&0xFF));
	}
	public void setShadowColor(int argb) {
		setProperty(TextAttribute.shadowColor, argb);
	}
	public void setShadowDx(float dx) {
		setProperty(TextAttribute.shadowDx, dx);
	}
	public void setShadowDy(float dy) {
		setProperty(TextAttribute.shadowDy, dy);
	}	
	public void setSpeed(float spd) {
		setProperty(TextAttribute.speed, spd);
	}
	
}
