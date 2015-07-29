package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

abstract class AbstractTextStyle implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final float EPSILON = .001f;

    protected final Map<ETextAttribute, Object> properties =
            new EnumMap<ETextAttribute, Object>(ETextAttribute.class);

    protected AbstractTextStyle() {
    }

    protected AbstractTextStyle(String fontName, float fontSize) {
        this(fontName, null, fontSize);
    }

    protected AbstractTextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        this();

        if (fontName != null) {
            properties.put(ETextAttribute.fontName, fontName);
        }
        if (fontStyle != null) {
            properties.put(ETextAttribute.fontStyle, fontStyle);
        }
        properties.put(ETextAttribute.fontSize, fontSize);
    }

    protected AbstractTextStyle(AbstractTextStyle m) {
        properties.putAll(m.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractTextStyle)) {
            return false;
        }

        AbstractTextStyle other = (AbstractTextStyle)obj;
        return properties.equals(other.properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<ETextAttribute, Object> entry : properties.entrySet()) {
            ETextAttribute key = entry.getKey();
            String value = key.toString(entry.getValue());
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append('|');
                }
                sb.append(key).append("=").append(value);
            }
        }
        return sb.toString();
    }

    public boolean hasProperty(ETextAttribute key) {
        return properties.containsKey(key);
    }

    public Object getProperty(ETextAttribute key, Object fallback) {
        Object val = properties.get(key);
        return (val != null ? val : fallback);
    }

    public String getFontName() {
        return getFontName(null);
    }
    public String getFontName(String fallback) {
        return (String)getProperty(ETextAttribute.fontName, fallback);
    }

    public EFontStyle getFontStyle() {
        return getFontStyle(EFontStyle.PLAIN);
    }
    public EFontStyle getFontStyle(EFontStyle fallback) {
        return (EFontStyle)getProperty(ETextAttribute.fontStyle, fallback);
    }

    public float getFontSize() {
        return getFontSize(12f);
    }
    public float getFontSize(float fallback) {
        return (Float)getProperty(ETextAttribute.fontSize, fallback);
    }

    public ETextAlign getAlign() {
        return getAlign(ETextAlign.NORMAL);
    }
    public ETextAlign getAlign(ETextAlign fallback) {
        return (ETextAlign)getProperty(ETextAttribute.align, fallback);
    }

    /**
     * @see #getColor(int)
     */
    public int getColor() {
        return getColor(0xFFFFFFFF);
    }
    /**
     * @return The text color packed into an int ({@code AARRGGBB})
     */
    public int getColor(int fallback) {
        return (Integer)getProperty(ETextAttribute.color, fallback);
    }

    public boolean isUnderlined() {
        return isUnderlined(false);
    }
    public boolean isUnderlined(boolean fallback) {
        return (Boolean)getProperty(ETextAttribute.underline, fallback);
    }

    public float getOutlineSize() {
        return getOutlineSize(0f);
    }
    public float getOutlineSize(float fallback) {
        return (Float)getProperty(ETextAttribute.outlineSize, fallback);
    }

    public int getOutlineColor() {
        return getOutlineColor(0);
    }
    public int getOutlineColor(int fallback) {
        return (Integer)getProperty(ETextAttribute.outlineColor, fallback);
    }

    public int getShadowColor() {
        return getShadowColor(0);
    }
    public int getShadowColor(int fallback) {
        return (Integer)getProperty(ETextAttribute.shadowColor, fallback);
    }

    public float getShadowDx() {
        return getShadowDx(0f);
    }
    public float getShadowDx(float fallback) {
        return (Float)getProperty(ETextAttribute.shadowDx, fallback);
    }

    public float getShadowDy() {
        return getShadowDy(0f);
    }
    public float getShadowDy(float fallback) {
        return (Float)getProperty(ETextAttribute.shadowDy, fallback);
    }

    public float getSpeed() {
        return getSpeed(1f);
    }
    public float getSpeed(float fallback) {
        return (Float)getProperty(ETextAttribute.speed, fallback);
    }

    public boolean hasOutline() {
        // Outline size is > 0 and outline color is not transparent
        return getOutlineSize() > EPSILON && !isTransparent(getOutlineColor());
    }

    public boolean hasShadow() {
        // Shadow offset != 0 and shadow color is not transparent
        return Math.abs(getShadowDx()) > EPSILON && Math.abs(getShadowDy()) > EPSILON
            && !isTransparent(getShadowColor());
    }

    private static boolean isTransparent(int argb) {
        return (argb & 0xFF000000) == 0;
    }

    protected static void extendProperties(Map<ETextAttribute, Object> out, Map<ETextAttribute, Object> ext) {
        for (Entry<ETextAttribute, Object> entry : ext.entrySet()) {
            ETextAttribute key = entry.getKey();
            Object oldval = out.get(key);
            Object extval = entry.getValue();
            out.put(key, key.extendValue(oldval, extval));
        }
    }

}
