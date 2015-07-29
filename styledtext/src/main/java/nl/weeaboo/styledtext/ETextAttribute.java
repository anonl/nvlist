package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.Locale;

public enum ETextAttribute {

    fontName(String.class),
    fontStyle(EFontStyle.class),
    fontSize(Float.class),
    align(ETextAlign.class),
    color(Integer.class),
    underline(Boolean.class),
    outlineSize(Float.class),
    outlineColor(Integer.class),
    shadowColor(Integer.class),
    shadowDx(Float.class),
    shadowDy(Float.class),
    speed(Float.class);

    private final Class<?> type;

    private ETextAttribute(Class<? extends Serializable> type) {
        this.type = type;
    }

    public boolean isValidType(Object val) {
        return val == null || type.isAssignableFrom(val.getClass());
    }

    public Object extendValue(Object base, Object ext) {
        if (base == null) return ext;
        if (ext == null) return base;

        // Custom behavior for certain attributes
        if (this == fontStyle) {
            return EFontStyle.combine((EFontStyle)base, (EFontStyle)ext);
        }

        // Default behavior: Overwrite value with ext
        return ext;
    }

    public Object fromString(String string) {
        if (string == null) return null;

        // By name
        if (this == color || this == outlineColor || this == shadowColor) {
            int val = (int)(Long.parseLong(string, 16) & 0xFFFFFFFFL);
            if (string.length() < 8) {
                val = 0xFF000000 | val;
            }
            return val;
        } else if (this == fontName) {
            return string.toLowerCase(Locale.ROOT);
        }

        // Enum types
        if (type == EFontStyle.class) {
            return EFontStyle.fromString(string);
        } else if (type == ETextAlign.class) {
            return ETextAlign.fromString(string);
        }

        // Simple types
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
        if (val == null) {
            return null;
        }

        if (this == color || this == outlineColor || this == shadowColor) {
            return String.format(Locale.ROOT, "%08x", (Integer)val);
        } else if (this == fontName) {
            return String.valueOf(val).toLowerCase(Locale.ROOT);
        }

        return String.valueOf(val);
    }

}
