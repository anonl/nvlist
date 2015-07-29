package nl.weeaboo.styledtext;

import java.util.Arrays;

public final class TextStyle extends AbstractTextStyle {

    private static final long serialVersionUID = 1L;
    private static final TextStyle DEFAULT_INSTANCE = new TextStyle();

    private TextStyle() {
        super();
    }

    public TextStyle(String fontName, float fontSize) {
        super(fontName, fontSize);
    }

    public TextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        super(fontName, fontStyle, fontSize);
    }

    TextStyle(AbstractTextStyle m) {
        super(m);
    }

    public static TextStyle defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public MutableTextStyle mutableCopy() {
        return new MutableTextStyle(this);
    }

    /**
     * @see AbstractTextStyle#toString()
     */
    public static TextStyle fromString(String string) {
        if (string == null) return null;

        TextStyle style = new TextStyle();

        String parts[] = string.split("\\|");
        for (int n = 0; n < parts.length; n++) {
            int index = parts[n].indexOf('=');

            ETextAttribute key = null;
            if (index < 0) {
                ETextAttribute keys[] = ETextAttribute.values();
                if (n >= 0 && n < keys.length) {
                    key = keys[n];
                }
            } else {
                String keyS = parts[n].substring(0, index).trim();
                key = ETextAttribute.valueOf(keyS);
            }

            if (key != null) {
                String valueS = parts[n].substring(index + 1).trim();
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

    public TextStyle extend(TextStyle ts) {
        return extend(this, ts);
    }

    public static TextStyle extend(TextStyle base, TextStyle ext) {
        if (ext == null) return base;
        if (base == null || base.equals(ext)) {
            return ext; // Styles are equal, nothing to do
        }

        TextStyle result = new TextStyle(base);
        extendProperties(result.properties, ext.properties);
        return result;
    }

    public static void extend(TextStyle[] out, TextStyle[] base, TextStyle[] ext) {
        if (out.length != base.length || base.length != ext.length) {
            throw new ArrayIndexOutOfBoundsException("Inconsistent lengths: out=" + out.length + ", base="
                    + base.length + ", ext=" + ext.length);
        }
        extend(out, 0, base, 0, ext, 0, out.length);
    }

    public static void extend(TextStyle[] out, int roff, TextStyle[] base, int boff, TextStyle[] ext, int eoff, int len) {
        TextStyle lastBase = null;
        TextStyle lastExt = null;
        TextStyle lastResult = null;
        for (int n = 0; n < len; n++) {
            TextStyle bs = base[boff++];
            TextStyle es = ext[eoff++];

            TextStyle result;
            if (lastBase == bs && lastExt == es) {
                // Optimization for when (base, ext) is the same as for the previous index
                result = lastResult;
            } else {
                result = TextStyle.extend(bs, es);

                lastBase = bs;
                lastExt = es;
                lastResult = result;
            }
            out[roff++] = result;
        }
    }

    static TextStyle[] replicate(TextStyle style, int times) {
        TextStyle[] result = new TextStyle[times];
        Arrays.fill(result, style);
        return result;
    }

}
