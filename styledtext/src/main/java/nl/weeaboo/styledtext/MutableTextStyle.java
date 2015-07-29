package nl.weeaboo.styledtext;

public final class MutableTextStyle extends AbstractTextStyle {

	private static final long serialVersionUID = 1L;

	public MutableTextStyle() {
		super();
	}

    public MutableTextStyle(String fontName, float fontSize) {
        super(fontName, fontSize);
    }

    public MutableTextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        super(fontName, fontStyle, fontSize);
    }

    public MutableTextStyle(AbstractTextStyle m) {
        super(m);
    }

	public MutableTextStyle copy() {
		return new MutableTextStyle(this);
	}
	public TextStyle immutableCopy() {
		return new TextStyle(this);
	}

	protected static int packColorFloat(float r, float g, float b, float a) {
		int ri = Math.max(0, Math.min(255, Math.round(255 * r)));
		int gi = Math.max(0, Math.min(255, Math.round(255 * g)));
		int bi = Math.max(0, Math.min(255, Math.round(255 * b)));
		int ai = Math.max(0, Math.min(255, Math.round(255 * a)));
		return (ai<<24)|(ri<<16)|(gi<<8)|(bi);
	}

    protected static int packColorInt(int r, int g, int b, int a) {
        int ri = Math.max(0, Math.min(255, r));
        int gi = Math.max(0, Math.min(255, g));
        int bi = Math.max(0, Math.min(255, b));
        int ai = Math.max(0, Math.min(255, a));
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

    public void extend(TextStyle ext) {
        if (ext == null || equals(ext)) return;

        extendProperties(properties, ext.properties);
	}

    public void removeProperty(ETextAttribute key) {
		properties.remove(key);
	}

    public void setProperty(ETextAttribute key, Object value) {
		if (key.isValidType(value)) {
			properties.put(key, value);
		}
	}

    public void setFontName(String name) {
        setProperty(ETextAttribute.fontName, name);
	}
	public void setFontStyle(EFontStyle style) {
        setProperty(ETextAttribute.fontStyle, style);
	}
	public void setFontSize(float size) {
        setProperty(ETextAttribute.fontSize, size);
	}
	public void setFont(String name, EFontStyle style, float size) {
		setFontName(name);
		setFontStyle(style);
		setFontSize(size);
	}
	public void setColor(float r, float g, float b) {
		setColor(r, g, b, 1);
	}
	public void setColor(float r, float g, float b, float a) {
		setColor(packColorFloat(r, g, b, a));
	}
	public void setColor(int r, int g, int b) {
		setColor(r, g, b, 255);
	}
	public void setColor(int r, int g, int b, int a) {
        setColor(packColorInt(r, g, b, a));
	}
	public void setColor(int argb) {
        setProperty(ETextAttribute.color, argb);
	}
	public void setAlign(ETextAlign a) {
        setProperty(ETextAttribute.align, a);
	}
	public void setUnderlined(boolean u) {
        setProperty(ETextAttribute.underline, u);
	}
	public void setOutlineSize(float s) {
        setProperty(ETextAttribute.outlineSize, s);
	}
	public void setOutlineColor(float r, float g, float b) {
		setOutlineColor(r, g, b, 1);
	}
	public void setOutlineColor(float r, float g, float b, float a) {
		setOutlineColor(packColorFloat(r, g, b, a));
	}
	public void setOutlineColor(int r, int g, int b) {
		setOutlineColor(r, g, b, 255);
	}
	public void setOutlineColor(int r, int g, int b, int a) {
        setOutlineColor(packColorInt(r, g, b, a));
	}
	public void setOutlineColor(int argb) {
        setProperty(ETextAttribute.outlineColor, argb);
	}
	public void setShadowColor(float r, float g, float b) {
		setShadowColor(r, g, b, 1);
	}
	public void setShadowColor(float r, float g, float b, float a) {
		setShadowColor(packColorFloat(r, g, b, a));
	}
	public void setShadowColor(int r, int g, int b) {
		setShadowColor(r, g, b, 255);
	}
	public void setShadowColor(int r, int g, int b, int a) {
        setShadowColor(packColorInt(r, g, b, a));
	}
	public void setShadowColor(int argb) {
        setProperty(ETextAttribute.shadowColor, argb);
	}
	public void setShadowDx(float dx) {
        setProperty(ETextAttribute.shadowDx, dx);
	}
	public void setShadowDy(float dy) {
        setProperty(ETextAttribute.shadowDy, dy);
	}
	public void setSpeed(float spd) {
        setProperty(ETextAttribute.speed, spd);
	}

}
